package uk.tim740.SkriptBot;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * Created by tim740 on 18/09/2016
 */

public class SkriptBot {
    static JDA jda;
    static long st = System.currentTimeMillis();

    public static void main(String[] args){
        if (args.length < 1){
            prSysE("No Token Specified");
            System.exit(0);
        }
        CmdSys.cmdSys(args);
        jda.getAccountManager().setGame("@Skript-Bot help");
        prSysI("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
        jda.getTextChannelById("227146011812823052").sendMessage("I was restarted so something new could of been added: `@Skript-Bot help`");

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                //System.out.print(">");
                String[] msg = System.console().readLine().split(" ");
                switch (msg[0]) {
                    case ("say"):
                        String id = "";
                        for (TextChannel c : jda.getGuildById("138464183946575874").getTextChannels()) {
                            if (msg[1].equals(c.getName())) id = c.getId();
                        }
                        if (!id.equals("")) {
                            ArrayList<String> cl = new ArrayList<>();
                            Collections.addAll(cl, msg);
                            for (int n = 0; n < cl.size(); n++) {
                                if (msg[n].contains("@")) {
                                    for (User ul : jda.getGuildById("138464183946575874").getUsers()) {
                                        if (cl.get(n).equals("@" + ul.getUsername().toLowerCase())) cl.set(n, ul.getAsMention());
                                        if (cl.get(n).equals("@" + ul.getUsername().toLowerCase() + ",")) cl.set(n, ul.getAsMention() + ",");
                                    }
                                }
                            }
                            String ns = "";
                            for (String clc : cl) {
                                ns += (" " + clc);
                            }
                            String ts = ns.replaceFirst("say", "").replaceFirst(msg[1], "").replaceFirst("   ", "");
                            jda.getTextChannelById(id).sendMessage(ts);
                            prSysI("[#" + msg[1] + "] Sent: '" + ts + "'");
                        }
                        break;
                    case "stop":
                        System.exit(0);
                    default:
                        System.out.println("CMDS: stop | say <channel> <text>");
                        break;
                }
            }
        }catch (Exception x){
            prSysE("Exception: " + x.getMessage());
        }
    }

    static String getJoinTxt(){
        return ("Welcome to **Skript-Chat**'s Discord! \n" +
                "You can get my commands by doing `@Skript-Bot help` \n\n" +
                "**Rules**: \n" +
                "   **1**. Use the right channels.\n" +
                "   **2**. Don't scam, spam or disrespect people. \n\n" +
                "If you an ***addon dev*** and would like the ***@Addon Developer Rank***, Proved the link to your post so we can prove that it's really you, then you will be able to post updates in ***#addon-updates*** (Please try to stick to the default format, your allowed to leave comments too if you wish) \n\n" +
                "If you would like to add a bot but need authorization you can message one of the staff members or ***@Staff*** \n\n" +
                "***@Staff*** If you need a Staff Member to make you ***Supporter***, they will need proof still. \n\n" +
                "If you need any more help ask a ***staff member*** :)");
    }
    static String msgBuilder(ArrayList<String> s) {
        String f = "";
        for (String j : s){
            f = (f + "\n" + j);
        }
        return f;
    }

    static void prSysI(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] [Skript-Bot]: " + s);
    }
    static void prSysE(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Error] [Skript-Bot]: " + s);
    }
}
