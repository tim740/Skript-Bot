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
  static long st = System.currentTimeMillis();
  private static CmdSys cmdSys = new CmdSys();

  public static void main(String[] args){
    new SkriptBot(args);
  }

  public SkriptBot(String[] args) {
    cmdSys.reg(args[0]);
    jda.getPresence().setGame(Game.watching("@Skript-Bot help"));
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).sendMessage("**Successfully reconnected to Skript-Chat**").queue();
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).getManager().setTopic("Last Restart: (" + new SimpleDateFormat("dd/MM/yy - HH:mm:ss").format(new Date()) + ") - took (" + (System.currentTimeMillis() - st) + "ms)").queue();
    while (true) {
      switch (System.console().readLine()) {
        case "rs":
          System.exit(0);
        default:
          System.out.println("<---[ Restart: rs ]--->");
          break;
      }
    }
  }

  static void prSysI(Guild g, TextChannel c, User u, String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + "[" + g.getName() + "] (#" + c.getName() + ") @" + u.getName() + "#" + u.getDiscriminator() + " - "  + s);
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).sendMessage("**" + g.getName() + "**: " + c.getAsMention() + " - " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysI(String g, User u, String s) {
    System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] " + "[" + g + "] @" + u.getName() + "#" + u.getDiscriminator() + " - "  + s);
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).sendMessage("**" + g + "**: " + u.getAsMention() + " - " + s).queue();
  }
}
