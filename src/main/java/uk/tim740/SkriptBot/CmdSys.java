package uk.tim740.SkriptBot;

import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.GuildManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static uk.tim740.SkriptBot.SkriptBot.*;

/**
 * Created by tim740 on 20/09/2016
 */
class CmdSys {

    static void cmdSys(String[] args) {
        try {
            jda = new JDABuilder().setBotToken(args[0]).addListener(new MessageListener()).buildBlocking();
            prSysI("Loaded: CmdSys.class");
        } catch (Exception x) {
            prSysE("Exception: " + x.getMessage());
            System.exit(0);
        }
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
                    prSysI("@" + u.getUsername() + " executed: '" + e.getMessage().getContent() + "'");
                    try {
                        switch (msg[1]) {
                            case "help": {
                                ArrayList<String> c = new ArrayList<>();
                                c.add("**COMMANDS** (All Commands start with `@Skript-Bot`)");
                                c.add("```xl");
                                c.add("   info - (Returns Info about me)");
                                c.add("   uptime - (Gets my uptime)");
                                c.add("   whois %player% - (Gets User Info)");
                                c.add("   links - (Returns useful links)");
                                c.add("   joinlink - (Returns the Join link for Skript-Chat)");
                                c.add("   jointxt - (Gets the First join text)");
                                c.add("   suggest %string% (Suggest an idea for me)");
                                c.add("```");
                                if (cl.contains("Staff")) {
                                    c.add("**ADMIN COMMANDS**");
                                    c.add("```xl");
                                    c.add("   setgame %string% - (Sets my game)");
                                    c.add("   setnick %player% %string% - (Sets a users nick)");
                                    c.add("   kick %player% - (kicks a user)");
                                    c.add("   say %string% - (Make me Speak)");
                                    c.add("```");
                                }
                                u.getPrivateChannel().sendMessage(msgBuilder(c));
                                e.getMessage().getChannel().sendMessage("I've send you a list of commands " + u.getAsMention());
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
                                String sc = e.getMessage().getContent().replace("@Skript-Bot", "").replaceFirst("suggest", "");
                                jda.getUserById("138441986314207232").getPrivateChannel().sendMessage("Suggestion from: " + u.getAsMention() + "\n ```" + sc + "```");
                                e.getMessage().getChannel().sendMessage("Your suggestion has been noted " + u.getAsMention());
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
                                }else {
                                    e.getMessage().deleteMessage();
                                }
                                break;
                            case "setgame":
                                if (cl.contains("Staff")) {
                                    String sg = e.getMessage().getContent().replaceFirst("@Skript-Bot", "").replaceFirst("setgame", "");
                                    jda.getAccountManager().setGame(sg);
                                }else{
                                    e.getMessage().deleteMessage();
                                }
                                break;
                            case "setnick":
                                if (cl.contains("Staff")) {
                                    String snn = e.getMessage().getContent().replaceFirst("@Skript-Bot", "").replaceFirst("setnick", "").replaceFirst(e.getMessage().getMentionedUsers().get(1).getAsMention(), "");
                                    new GuildManager(e.getGuild()).setNickname(e.getMessage().getMentionedUsers().get(1), snn);
                                    e.getMessage().getChannel().sendMessage(u.getAsMention() + " set '" + e.getMessage().getMentionedUsers().get(1).getAsMention() + "' nickname to " + snn);
                                }else{
                                    e.getMessage().deleteMessage();
                                }
                                break;
                            case "say":
                                e.getMessage().deleteMessage();
                                if (cl.contains("Staff")) {
                                    String sc1 = e.getMessage().getContent().replaceFirst("@Skript-Bot", "").replaceFirst("say", "").replace("@everyone", "").replace("@here", "");
                                    for (User tu : e.getMessage().getMentionedUsers()) {
                                        sc1 = sc1.replace("@" + tu.getUsername(), tu.getAsMention());
                                    }
                                    e.getMessage().getChannel().sendMessage(sc1);
                                }
                                break;
                            default:
                                e.getMessage().deleteMessage();
                                e.getMessage().getChannel().sendMessage("Did you mean `@Skript-Bot help` " + u.getAsMention() + "?");
                                break;
                        }
                    }catch (Exception x) {
                        prSysE("Exception: " + x.getMessage());
                    }
                }
            }
        }
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent e) {
            e.getUser().getPrivateChannel().sendMessage(getJoinTxt());
            jda.getTextChannelById("138464183946575874").sendMessage("Welcome " + e.getUser().getAsMention() + " to Skript-Chat!");
            prSysI("@" + e.getUser().getUsername() + " has joined Skript-Chat!");
        }
    }
}