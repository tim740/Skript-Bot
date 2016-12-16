package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by tim740 on 18/09/2016
 */

public class SkriptBot {
    static JDA jda;
    static String skcid = ("138464183946575874");
    static long st = System.currentTimeMillis();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.exit(0);
        }
        CmdSys.cmdSys(args);
        jda.getPresence().setGame(Game.of("@Skript-Bot help"));
        prSysI("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
        jda.getTextChannelById("227146011812823052").sendMessage("Restarted!, new things may have been added: `@Skript-Bot help`").queue();

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                String umsg = System.console().readLine();
                String[] msg = umsg.split(" ");
                switch (msg[0]) {
                    case ("sg"): {
                        jda.getPresence().setGame(Game.of(umsg.replaceFirst(msg[0] + "", "")));
                    } case ("!"): {
                        String id = "";
                        for (TextChannel c : jda.getGuildById(skcid).getTextChannels()) {
                            if (msg[1].equals(c.getName())) id = c.getId();
                        }
                        if (!id.equals("")) {
                            ArrayList<String> cl = new ArrayList<>();
                            Collections.addAll(cl, msg);
                            for (int n = 0; n < 2; n++) cl.remove(0);
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
                            prSysI("[#" + msg[1] + "] Sent: '" + ns.replaceFirst(" ", "") + "'");
                        }
                        break;
                    } case "rs": {
                        System.exit(0);
                    } default: {
                        System.out.println("------------------------------");
                        System.out.println("> SetGame: sg <text>");
                        System.out.println("> Say: ! <channel> <text>");
                        System.out.println("> Restart: rs");
                        System.out.println("------------------------------");
                        break;
                    }
                }
            }
        } catch (Exception x) {
            prSysE("Exception: " + x.getMessage());
        }
    }

    static void prSysI(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + s);
    }

    static void prSysE(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Error] " + s);
    }
}
