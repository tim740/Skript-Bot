package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tim740 on 18/09/2016
 */
public class SkriptBot {
  static JDA jda;
  static String skcid = "138464183946575874";
  static String skcaid = "252856353217970177";
  static String lcid = "327617436713091072";
  static long st = System.currentTimeMillis();

  public static void main(String[] args) {
    CmdSys.cmdSys(args[0]);
    jda.getPresence().setGame(Game.of("@Skript-Bot help"));
    CmdSys.reg();
    jda.getGuildById(skcid).getTextChannelById(lcid).sendMessage("**Successfully reconnected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!**").queue();
    while (true) {
      switch (System.console().readLine()) {
        case "rs": {
          System.exit(0);
        }
        default: {
          System.out.println("<---[ Restart: rs ]--->");
          break;
        }
      }
    }
  }

  static void prSysI(Guild g, TextChannel c, User u, String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + "[" + g.getName() + "] (#" + c.getName() + ") @" + u.getName() + "#" + u.getDiscriminator() + " - "  + s);
    jda.getGuildById(skcid).getTextChannelById(lcid).sendMessage("**" + g.getName() + "**: " + c.getAsMention() + " - " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysI(Guild g, User u, String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + "[" + g.getName() + "] @" + u.getName() + "#" + u.getDiscriminator() + " - "  + s);
    jda.getGuildById(skcid).getTextChannelById(lcid).sendMessage("**" + g.getName() + "**: " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysI(User u, String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + "[Private] @" + u.getName() + "#" + u.getDiscriminator() + " - " + s);
    jda.getGuildById(skcid).getTextChannelById(lcid).sendMessage("**Private**: " + u.getAsMention() + " - " + s).queue();
  }
}
