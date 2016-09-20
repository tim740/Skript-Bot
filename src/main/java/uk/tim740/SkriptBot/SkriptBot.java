package uk.tim740.SkriptBot;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.GuildManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * Created by tim740 on 18/09/2016
 */

public class SkriptBot {
    private static JDA jda;
    private static long st = System.currentTimeMillis();

    public static void main(String[] args){
        if (args.length < 1){
            prSys("No Token Specified");
            System.exit(0);
        }
        try {
            jda = new JDABuilder().setBotToken(args[0]).addListener(new MessageListener()).buildBlocking();
        } catch (Exception e) {
            prSys(e.getMessage());
            System.exit(0);
        }
        jda.getAccountManager().setGame("@Skript-Bot help");
        prSys("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - st) + "ms!");
    }
    private static class MessageListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent e){
            if (!e.getMessage().getAuthor().getId().equals("227067574469394432")) {
                if (e.getMessage().getContent().startsWith("@Skript-Bot")) {
                    String[] msg = e.getMessage().getContent().split(" ");
                    User u = e.getMessage().getAuthor();
                    ArrayList<String> cl = e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new));
                    //e.getMessage().deleteMessage();
                    prSys("@" + u.getUsername() + " executed: '" + e.getMessage().getContent() + "'");
                    switch (msg[1]) {
                        case "help": {//send message as skript bot
                            ArrayList<String> c = new ArrayList<>();
                            c.add("**COMMANDS** (All Commands start with `@Skript-Bot`)");
                            c.add("```xl");
                            c.add("   info - (Returns Info about me)");
                            c.add("   uptime - (Gets my uptime)");
                            c.add("   whois <user> - (Gets User Info)");
                            c.add("   links - (Returns useful links)");
                            c.add("   joinlink - (Returns the Join link for Skript-Chat)");
                            c.add("   jointxt - (Gets the First join text)");
                            c.add("   suggest <idea> (Suggest an idea for Skript-Bot)");
                            c.add("```");
                            if (cl.contains("Staff")) {
                                c.add("**ADMIN COMMANDS**");
                                c.add("```xl");
                                c.add("   setgame <text> - (Sets my game)");
                                c.add("   setnick <user> <text> - (Sets a users nick)");
                                c.add("   kick <user> - (kicks a user)");
                                c.add("   stop - (Stops Skript-Bot)");
                                c.add("```");
                            }
                            u.getPrivateChannel().sendMessage(msgBuilder(c));
                            //e.getMessage().getChannel().sendMessage("I've send you a list of commands " + u.getAsMention());
                            break;
                        }
                        case "info": {
                            ArrayList<String> c = new ArrayList<>();
                            c.add("Created: @tim740#1139 (18/09/2016)");
                            c.add("Website: <https://tim740.github.io/>");
                            c.add("Source: <https://github.com/tim740/Skript-Bot>");
                            c.add("JDA Api: <https://github.com/DV8FromTheWorld/JDA>");
                            u.getPrivateChannel().sendMessage(msgBuilder(c));
                            break;
                        }
                        case "uptime": {
                            long ts = (System.currentTimeMillis() - st) / 1000;
                            long tm = ts / 60;
                            long th = tm / 60;
                            e.getMessage().getChannel().sendMessage(u.getAsMention() + " My current uptime is: `" + ((th / 24) + "d " + th % 24 + "h " + tm % 60 + "m " + ts % 60 + "s`"));
                            break;
                        }
                        case "whois": {
                            User wu = e.getMessage().getMentionedUsers().get(1);
                            ArrayList<String> c = new ArrayList<>();
                            c.add("**ID**: " + wu.getId());
                            c.add("**Name**: " + wu.getUsername());
                            c.add("**Online**: " + wu.getOnlineStatus());
                            c.add("**Game**: " + wu.getCurrentGame().getName());
                            c.add("**Bot**: " + wu.isBot());
                            u.getPrivateChannel().sendMessage(msgBuilder(c));
                            break;
                        }
                        case "suggest":
                            String sn = e.getMessage().getContent().replace("@Skript-Bot", "").replaceFirst("suggest", "");
                            jda.getUserById("138441986314207232").getPrivateChannel().sendMessage(u.getAsMention() + " Suggested:\n" + sn);
                            u.getPrivateChannel().sendMessage("Your suggestion has been noted " + u.getAsMention());
                            break;
                        case "links": {
                            ArrayList<String> c = new ArrayList<>();
                            c.add("**USEFUL LINKS**");
                            c.add("   **Bensku's Skript**: <https://github.com/bensku/Skript/releases>");
                            c.add("   **Virustotal's skQuery**: <https://github.com/SkriptLegacy/skquery/releases>");
                            c.add("   **Latest Aliases**: <https://forums.skunity.com/t/40?u=tim740>");
                            c.add("   **Formatting**: <https://support.discordapp.com/hc/en-us/articles/210298617>");
                            u.getPrivateChannel().sendMessage(msgBuilder(c));
                            break;
                        }
                        case "joinlink":
                            e.getMessage().getChannel().sendMessage("Skript-Chat Join Link: https://discord.gg/bxaPNjN");
                            break;
                        case "jointxt":
                            u.getPrivateChannel().sendMessage(getJoinTxt());
                            break;
                        case "kick":
                            if (cl.contains("Staff")) {
                                if (msg[2].contains("@")) {
                                    new GuildManager(e.getGuild()).kick(msg[2]);
                                    e.getMessage().getChannel().sendMessage("Kicked: " + e.getMessage().getMentionedUsers().get(1));
                                }
                            }
                            break;
                        case "setgame":
                            if (cl.contains("Staff")) {
                                String sg = e.getMessage().getContent().replace("@Skript-Bot", "").replaceFirst("setgame", "");
                                jda.getAccountManager().setGame(sg);
                            }
                            break;
                        case "setnick":
                            if (cl.contains("Staff")) {
                                if (!e.getMessage().getMentionedUsers().get(1).getId().equals("138441986314207232")) {
                                    String snn = e.getMessage().getContent().replace("@Skript-Bot", "").replaceFirst("setnick", "").replaceFirst(msg[2], "");
                                    new GuildManager(e.getGuild()).setNickname(e.getMessage().getMentionedUsers().get(1), snn);
                                    e.getMessage().getChannel().sendMessage(u.getAsMention() + " set '" + msg[2] + "' nickname to " + e.getMessage().getMentionedUsers().get(1));
                                }else{
                                    e.getMessage().getChannel().sendMessage(u.getAsMention() + " You cannot edit @tim740's nickname!");
                                }
                            }
                            break;
                        case "stop":
                            e.getMessage().deleteMessage();
                            if (e.getMessage().getAuthor().getId().equals("138441986314207232")) {
                                System.exit(0);
                            }
                            break;
                        default:
                            e.getMessage().getChannel().sendMessage("Did you mean `@Skript-Bot help` " + u.getAsMention() + "?");
                            break;
                    }
                }
            }
        }
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent e) {
            e.getUser().getPrivateChannel().sendMessage(getJoinTxt());
            jda.getTextChannelById("138464183946575874").sendMessage("Welcome " + e.getUser().getAsMention() + " to Skript-Chat!");
            prSys("@" + e.getUser().getUsername() + " has joined Skript-Chat!");
        }
    }
    private static String getJoinTxt(){
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
    private static String msgBuilder(ArrayList<String> s) {
        String f = "";
        for (String j : s){
            f = (f + "\n" + j);
        }
        return f;
    }
    private static void prSys(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] [Skript-Bot]: " + s);
    }
}
