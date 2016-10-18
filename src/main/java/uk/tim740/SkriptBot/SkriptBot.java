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

    public static void main(String[] args) {
        if (args.length < 1) {
            prSysE("No Token Specified");
            System.exit(0);
        }
        CmdSys.cmdSys(args);
        jda.getAccountManager().setGame("@Skript-Bot help");
        prSysI("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
        jda.getTextChannelById("227146011812823052").sendMessage("Restarted!, new things may have been added: `@Skript-Bot help`");

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                String[] msg = System.console().readLine().split(" ");
                switch (msg[0]) {
                    case ("!"): {
                        String id = "";
                        for (TextChannel c : jda.getGuildById("138464183946575874").getTextChannels()) {
                            if (msg[1].equals(c.getName())) id = c.getId();
                        } if (!id.equals("")) {
                            ArrayList<String> cl = new ArrayList<>();
                            Collections.addAll(cl, msg);
                            for (int n = 0; n < 2; n++) cl.remove(0);
                            for (int n = 0; n < cl.size(); n++) {
                                if (cl.get(n).contains("@")) {
                                    for (User ul : jda.getGuildById("138464183946575874").getUsers()) {
                                        if (cl.get(n).equals("@" + ul.getUsername().toLowerCase())) {
                                            cl.set(n, ul.getAsMention());
                                            break;
                                        }
                                        if (cl.get(n).equals("@" + ul.getUsername().toLowerCase() + ",")) {
                                            cl.set(n, ul.getAsMention() + ",");
                                            break;
                                        }
                                    }
                                }
                                for (TextChannel ch : jda.getGuildById("138464183946575874").getTextChannels()) {
                                    if (cl.get(n).equals("#" + ch.getName())) cl.set(n, ch.getAsMention());
                                }
                            }
                            String ns = "";
                            for (String clc : cl) {
                                ns += (" " + clc);
                            }
                            jda.getTextChannelById(id).sendMessage(ns);
                            prSysI("[#" + msg[1] + "] Sent: '" + ns.replaceFirst(" ", "") + "'");
                        }
                        break;
                    } case "rs": {
                        System.exit(0);
                    } default: {
                        System.out.println("> Restart: rs");
                        System.out.println("> Say: ! <channel> <text>");
                        break;
                    }
                }
            }
        } catch (Exception x) {
            prSysE("Exception: " + x.getMessage());
        }
    }

    static String msgBuilder(ArrayList<String> s) {
        String f = "";
        for (String j : s) {
            f += ("\n" + j);
        }
        return f;
    }

    static void prSysI(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + s);
    }

    static void prSysE(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Error] " + s);
    }
}
