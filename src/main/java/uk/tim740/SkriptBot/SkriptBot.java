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
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Created by tim740 on 18/09/2016
 */

public class SkriptBot {
    private static JDA jda;

    public static void main(String[] args){
        long s = System.currentTimeMillis();
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
        prSys("Successfully Connected to Skript-Chat, took " + (System.currentTimeMillis() - s) + "ms!");
    }
    private static class MessageListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent e){
            if (!e.getMessage().getAuthor().getId().equals("227067574469394432")) {
                if (e.getMessage().getContent().startsWith("@Skript-Bot")) {
                    String[] msg = e.getMessage().getContent().split(" ");
                    User u = e.getMessage().getAuthor();
                    ArrayList<String> cl = e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new));
                    e.getMessage().deleteMessage();
                    prSys("@" + u.getUsername() + " executed: '" + e.getMessage().getContent() + "'");
                    if (Objects.equals(msg[1], "help")) {
                        u.getPrivateChannel().sendMessage(
                                "**COMMANDS**: (All Commands start with `@Skript-Bot`)\n" +
                                        "   **info** - (Returns Info about me)\n" +
                                        "   **links** - (Returns useful links)\n" +
                                        "   **joinlink** - (Returns the Join link for Skript-Chat)\n" +
                                        "   **jointxt** - (Gets the First join text)\n" +
                                        "   **suggest <idea>** (Suggest an idea for Skript-Bot)");
                        if (cl.contains("Staff")) {
                            u.getPrivateChannel().sendMessage(
                                    "**ADMIN COMMANDS**: \n" +
                                            "   **kick <user>** - (kicks a user)\n" +
                                            "   **stop** - (Stops Skript-Bot)");
                        }
                    } else if (Objects.equals(msg[1], "info")) {
                        u.getPrivateChannel().sendMessage(
                                "Created: @tim740#1139 (18/09/2016)\n" +
                                        "Website: <https://tim740.github.io/>\n" +
                                        "Source: <https://github.com/tim740/Skript-Bot>\n" +
                                        "JDA Api: <https://github.com/DV8FromTheWorld/JDA>");
                    } else if (Objects.equals(msg[1], "suggest")) {
                        String sn = e.getMessage().getContent().replace("@Skript-Bot", "").replaceFirst("suggest", "");
                        jda.getUserById("138441986314207232").getPrivateChannel().sendMessage(u.getAsMention() + " Suggested:\n" + sn);
                        u.getPrivateChannel().sendMessage("Your suggestion has been noted " + u.getAsMention());
                    } else if (Objects.equals(msg[1], "links")) {
                        u.getPrivateChannel().sendMessage(
                                "**USEFUL LINKS**\n" +
                                        "   **Bensku's Skript**: <https://github.com/bensku/Skript/releases>\n" +
                                        "   **Virustotal's skQuery**: <https://github.com/SkriptLegacy/skquery/releases>\n" +
                                        "   **Latest Aliases**: <https://forums.skunity.com/t/40?u=tim740>\n" +
                                        "   **Formatting**: <https://support.discordapp.com/hc/en-us/articles/210298617>");
                    } else if (Objects.equals(msg[1], "joinlink")) {
                        e.getMessage().getChannel().sendMessage("Skript-Chat Join Link: https://discord.gg/bxaPNjN");
                    } else if (Objects.equals(msg[1], "jointxt")) {
                        u.getPrivateChannel().sendMessage(getJoinTxt());
                    } else if (Objects.equals(msg[1], "kick")) {
                        if (cl.contains("Staff")) {
                            if (msg[2].contains("@")) {
                                new GuildManager(e.getGuild()).kick(u);
                                e.getMessage().getChannel().sendMessage("Kicked: " + u.getUsername());
                            }
                        }
                    } else if (Objects.equals(msg[1], "stop")) {
                        if (e.getMessage().getAuthor().getId().equals("138441986314207232")) {
                            System.exit(0);
                        }
                    } else {
                        e.getMessage().getChannel().sendMessage("Did you mean `@Skript-Bot help` " + u.getAsMention() +"?");
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
    private static void prSys(String s) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] [Info] [Skript-Bot]: " + s);
    }
}
