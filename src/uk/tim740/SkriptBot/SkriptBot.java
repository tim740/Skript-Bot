package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

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
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).getManager().setTopic("Last Restart: (" + new SimpleDateFormat("dd/MM/yy - HH:mm:ss").format(new Date()) + ") - took (" + (System.currentTimeMillis() - st) + "ms)").queue();
    Scanner s = new Scanner(System.in);
/*    while (s.hasNext()) {
      switch (s.nextLine()) {
        case "rs":
          System.exit(0);
        default:
          System.out.println("<---[ Restart: rs ]--->");
          break;
      }
    }*/
  }

  static void prSysI(Guild g, TextChannel c, User u, String s) {
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).sendMessage("`" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "` - **" + g.getName() + "**: " + c.getAsMention() + " - " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysI(String g, User u, String s) {
    cmdSys.GO.getTextChannelById(cmdSys.LC_ID).sendMessage("`" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "` - **" + g + "**: " + u.getAsMention() + " - " + s).queue();
  }
}
