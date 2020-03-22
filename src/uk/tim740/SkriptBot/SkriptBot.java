package uk.tim740.SkriptBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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
    //jda.getPresence().setGame(Game.watching("@Skript-Bot help"));
    //cmdSys.GO.getTextChannelById(cmdSys.CONSOLE).getManager().setTopic("Last Restart: (" + new SimpleDateFormat("dd/MM/yy - HH:mm:ss").format(new Date()) + ") - took (" + (System.currentTimeMillis() - st) + "ms)").queue();
  }

  static void prSysI(Guild g, TextChannel c, User u, String s) {
    g.getTextChannelsByName("Console", true).get(0).sendMessage("`" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "` - **" + g.getName() + "**: " + c.getAsMention() + " - " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysI(Guild g, User u, String s) {
    g.getTextChannelsByName("Console", true).get(0).sendMessage("`" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "` - **" + g.getName() + "**: " + u.getAsMention() + " - " + s).queue();
  }
  static void prSysIP(String g, User u, String s) {
    cmdSys.GO.getTextChannelById(cmdSys.CONSOLE).sendMessage("`" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "` - **" + g + "**: " + u.getAsMention() + " - " + s).queue();
  }
}
