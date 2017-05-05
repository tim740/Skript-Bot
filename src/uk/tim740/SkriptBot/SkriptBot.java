package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tim740 on 18/09/2016
 */
public class SkriptBot {
  static JDA jda;
  static String skcid = ("138464183946575874");
  static String skcaid = ("252856353217970177");
  static long st = System.currentTimeMillis();

  public static void main(String[] args) {
    CmdSys.cmdSys(args[0]);
    jda.getPresence().setGame(Game.of("@Skript-Bot help"));
    prSysI("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
    try {
      while (true) {
        switch (System.console().readLine()) {
          case "rs": {
            System.exit(0);
          } default: {
            System.out.println("<---[ Restart: rs ]--->");
            break;
          }
        }
      }
    } catch (Exception x) {
      writeDebug(x);
    }
  }

  static void prSysI(String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + s);
  }

  static void writeDebug(Exception x) {
    try {
      System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Exception] " + x.getMessage() + " (Click 'Debug')");
      StringWriter sw = new StringWriter();
      x.printStackTrace(new PrintWriter(sw));
      String s = ("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + sw.toString());
      sw.close();
      Path pth = Paths.get("debug.log");
      if (Files.readAllLines(pth).get(Math.toIntExact(Files.lines(pth, Charset.defaultCharset()).count()) -1).equals("")) s = ("\n" + s);
      Files.write(pth, (s).getBytes(), StandardOpenOption.APPEND);
    } catch (Exception xe) {
      xe.printStackTrace();
    }
  }
}
