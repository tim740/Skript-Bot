package uk.tim740.SkriptBot;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.Desktop;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by tim740 on 18/09/2016
 */
public class SkriptBot extends Application {
    static JDA jda;
    private static String token;
    static String skcid = ("138464183946575874");
    static long st = System.currentTimeMillis();
    private static String lcmd = "";

    public static void main(String[] args) {
        token = args[0];
        Application.launch(SkriptBot.class);
    }

    @Override
    public void start(Stage ps) throws Exception {
        try {
            Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
            ps.setTitle("SkriptBot for Discord");
            ps.getIcons().add(new Image(getClass().getResourceAsStream("/resources/ico.png")));
            ps.setOnCloseRequest(e -> System.exit(0));
            AnchorPane ap = new AnchorPane();
            ap.setPrefSize(800,475);
            ap.setId("ap");
            ap.getStylesheets().add(this.getClass().getResource("/resources/main.css").toExternalForm());
            ps.setScene(new Scene(ap));

            ScrollPane sp = new ScrollPane();
            sp.setLayoutX(5);
            sp.setLayoutY(5);
            sp.setPrefSize(790, 435);
            sp.setId("sp");
            ap.getChildren().add(sp);

            TextArea ta = new TextArea();
            ta.setLayoutX(5);
            ta.setLayoutY(5);
            ta.setPrefSize(790, 435);
            ta.setEditable(false);
            ta.setWrapText(true);
            PrintStream pss = new PrintStream(new Console(ta), true);
            System.setOut(pss);
            System.setErr(pss);
            ta.setId("ta");
            ap.getChildren().add(ta);

            exec();
            ArrayList<String> chl = new ArrayList<>();
            for (TextChannel chn : jda.getGuildById(skcid).getTextChannels()) {
                chl.add("#" + chn.getName());
            }
            ChoiceBox<String> cb = new ChoiceBox<>(FXCollections.observableList(chl));
            cb.setLayoutX(5);
            cb.setLayoutY(445);
            cb.setPrefSize(102, 25);
            cb.setId("cb");
            cb.getSelectionModel().select("#bot-testing");
            ap.getChildren().add(cb);

            TextField tf = new TextField();
            tf.setLayoutX(112);
            tf.setLayoutY(445);
            tf.setPrefSize(619,25);
            tf.setOnKeyPressed(ke -> {
                if (ke.getCode() == KeyCode.ENTER) {
                    if (!cb.getValue().isEmpty()) {
                        String cmd = tf.getText();
                        execSay(cb.getValue().replaceFirst("#", ""), cmd);
                        lcmd = cmd;
                        tf.setText("");
                    }
                } else if (ke.getCode() == KeyCode.UP){
                    tf.setText(lcmd);
                } else if (ke.getCode() == KeyCode.DOWN) {
                    tf.setText("");
                }
            });
            tf.setId("tf");
            ap.getChildren().add(tf);

            Button b = new Button();
            b.setText("Debug");
            b.setLayoutX(736);
            b.setLayoutY(445);
            b.setPrefSize(59, 25);
            b.setMnemonicParsing(false);
            b.setOnAction(e -> {
                try {
                    Desktop.getDesktop().open(new File("debug.log"));
                } catch (IOException x) {
                    writeDebug(x);
                }
            });
            b.setId("b");
            ap.getChildren().add(b);
            ps.show();
        } catch (Exception x) {
            writeDebug(x);
        }
    }

    private void execSay(String cha, String s) {
        try {
            String id = "";
            for (TextChannel c : jda.getGuildById(skcid).getTextChannels()) {
                if (cha.equals(c.getName())) id = c.getId();
            }
            if (!id.equals("")) {
                ArrayList<String> cl = new ArrayList<>();
                Collections.addAll(cl, s.split(" "));
                for (int n = 0; n < cl.size(); n++) {
                    if (cl.get(n).contains("@")) {
                        for (Member ul : jda.getGuildById(skcid).getMembers()) {
                            if (cl.get(n).equals("@" + ul.getUser().getName().toLowerCase())) {
                                cl.set(n, ul.getAsMention());
                                break;
                            }
                            if (cl.get(n).equals("@" + ul.getEffectiveName().toLowerCase() + ",")) {
                                cl.set(n, ul.getAsMention() + ",");
                                break;
                            }
                        }
                    }
                    for (TextChannel ch : jda.getGuildById(skcid).getTextChannels()) {
                        if (cl.get(n).equals("#" + ch.getName())) cl.set(n, ch.getAsMention());
                    }
                }
                String ns = "";
                for (String clc : cl) {
                    ns += (" " + clc);
                }
                jda.getTextChannelById(id).sendMessage(ns).queue();
                prSysI("[#" + cha + "] Sent: '" + ns.replaceFirst(" ", "") + "'");
            }
        } catch (Exception x) {
            writeDebug(x);
        }
    }

    private void exec() {
        CmdSys.cmdSys(token);
        jda.getPresence().setGame(Game.of("@Skript-Bot help"));
        prSysI("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
        //jda.getTextChannelById("227146011812823052").sendMessage("Restarted `@Skript-Bot help`").queue();
    }

    static void prSysI(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + s);
    }

    static void writeDebug(Exception x) {
        try {
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Exception] " + x.getMessage() + " (Click 'View Debug')");
            StringWriter sw = new StringWriter();
            x.printStackTrace(new PrintWriter(sw));
            String s = sw.toString();
            sw.close();
            File pth = new File("debug.log");
            ArrayList<String> cl = new ArrayList<>();
            cl.addAll(Files.readAllLines(pth.toPath(), Charset.defaultCharset()));
            cl.add("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + s);
            BufferedWriter bw = new BufferedWriter(new FileWriter(pth));
            for (String aCl : cl.toArray(new String[cl.size()])) {
                bw.write(aCl);
                bw.newLine();
            }
            bw.close();
        } catch (Exception xe) {
            xe.printStackTrace();
        }
    }
}

class Console extends OutputStream {
    private TextArea output;

    Console(TextArea ta) {
        output = ta;
    }

    @Override
    public void write(int b) throws IOException {
        output.appendText(String.valueOf((char) b));
    }
}
