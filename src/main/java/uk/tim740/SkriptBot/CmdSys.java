package uk.tim740.SkriptBot;

import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.OnlineStatus;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.MessageUpdateEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.GuildManager;
import net.dv8tion.jda.utils.InviteUtil;
import net.dv8tion.jda.utils.MiscUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.tim740.SkriptBot.SkriptBot.*;

/**
 * Created by tim740 on 20/09/2016
 */
class CmdSys {
    private static Pattern tf = Pattern.compile("^(?:true|false),? +the +person +below +me +.+$");

    static void cmdSys(String[] args) {
        try {
            jda = new JDABuilder().setBotToken(args[0]).addListener(new MessageListener()).buildBlocking();
            prSysI("Loaded: Command System!");
        } catch (Exception x) {
            prSysE("Exception: " + x.getMessage());
            System.exit(0);
        }
    }

    private static class MessageListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent e) {
            if (!e.getMessage().getAuthor().getId().equals("227067574469394432")) {
                if (e.getChannel().getId().equals("237960698854899713")) {
                    if (!e.getGuild().getRolesForUser(e.getMessage().getAuthor()).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                        if (!tf.matcher(e.getMessage().getContent().toLowerCase()).find()) e.getMessage().deleteMessage();
                    }
                } else if (e.getMessage().getContent().startsWith("@Skript-Bot")) {
                    String[] msg = e.getMessage().getContent().split(" ");
                    String umsg = e.getMessage().getContent().replaceFirst(msg[0] + " ", "");
                    User u = e.getMessage().getAuthor();
                    prSysI("(#" + jda.getTextChannelById(e.getChannel().getId()).getName() + ") @" + u.getUsername() + " executed: '" + e.getMessage().getContent() + "'");
                    try {
                        switch (msg[1].toLowerCase()) {
                            case "help": {
                                ArrayList<String> c = new ArrayList<>();
                                c.add("**COMMANDS** (All Commands start with `@Skript-Bot`)");
                                c.add("```");
                                c.add("   info - (Returns Info about me)");
                                c.add("   info (skript-chat|skc) - (Returns chat info)");
                                c.add("   emotes - (Returns all the Emotes)");
                                c.add("   version (aliases) - (Returns the latest version)");
                                c.add("   uptime - (Returns my uptime & ping)");
                                c.add("   whois %player% - (Returns User Info)");
                                c.add("   skunity %string% - (Lookup on skUnity Docs)");
                                c.add("   sku-status - (Checks if skUnity Forums is up)");
                                c.add("   links - (Returns useful links)");
                                c.add("   joinlink - (Returns the Join link for Skript-Chat)");
                                c.add("   suggest %string% (Suggest an idea for me)");
                                c.add("   convert (bin2txt|txt2bin) %string% (Convert things)");
                                c.add("```");
                                if (e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                                    c.add("**ADMIN COMMANDS**");
                                    c.add("```");
                                    c.add("   prune %integer% - (Removes x amount of msgs, 0 - 50)");
                                    c.add("   setgame %string% - (Sets my game)");
                                    c.add("   kick %player% - (kicks a user)");
                                    c.add("   say %string% - (Make me Speak)");
                                    c.add("```");
                                }
                                u.getPrivateChannel().sendMessage(msgBuilder(c));
                                e.getMessage().getChannel().sendMessage("I've send you a list of commands " + u.getAsMention());
                                break;
                            } case "info": {
                                ArrayList<String> c = new ArrayList<>();
                                if (umsg.contains("skript-chat") || umsg.contains("skc")) {
                                    int on = 0, off = 0, bot = 0;
                                    for (User s : e.getGuild().getUsers()) {
                                        if (s.getOnlineStatus().equals(OnlineStatus.ONLINE) || s.getOnlineStatus().equals(OnlineStatus.AWAY)) {
                                            on++;
                                        } else if (s.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                                            off++;
                                        }
                                        if (s.isBot()) {
                                            bot++;
                                        }
                                    }
                                    c.add("**Here's the information on Skript-chat!**");
                                    c.add("```");
                                    c.add("Creator: " + e.getGuild().getOwner().getUsername());
                                    c.add("Online Users: " + on + "/" + e.getGuild().getUsers().size());
                                    c.add("Offline Users: " + off + "/" + e.getGuild().getUsers().size());
                                    c.add("Bots: " + bot + "/" + e.getGuild().getUsers().size());
                                    c.add("Text/Voice Channels: " + e.getGuild().getTextChannels().size() + "/" + e.getGuild().getVoiceChannels().size());
                                    c.add("```");
                                } else {
                                    c.add("**Here's my information!**");
                                    c.add("Created: @tim740#1139 (18/09/2016)");
                                    c.add("Website: <https://tim740.github.io/>");
                                    c.add("Source: <https://github.com/tim740/Skript-Bot>");
                                    c.add("JDA " + JDAInfo.VERSION +  ": <https://github.com/DV8FromTheWorld/JDA>");
                                }
                                e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                break;
                            } case "emotes": {
                                ArrayList<String> c = new ArrayList<>();
                                String el = "";
                                for (Emote s : e.getGuild().getEmotes()) {
                                    el = (el + " " + s.getAsEmote());
                                }
                                c.add("**Here's a list of all the emotes in Skript-chat!**");
                                c.add(el);
                                e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                break;
                            } case "version": {
                                if (msg[2].equals("aliases")) {
                                    ArrayList<String> c = new ArrayList<>();
                                    BufferedReader ur = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/tim740/skAliases/master/version.txt").openStream()));
                                    c.add(u.getAsMention() + " here's latest aliases version: `" + ("v" + ur.readLine()) + "`");
                                    ur.close();
                                    c.add("Latest aliases thread: <https://forums.skunity.com/t/40?u=tim740>");
                                    e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                }
                                break;
                            } case "uptime": {
                                long ts = (System.currentTimeMillis() - st) / 1000;
                                long tm = ts / 60;
                                long th = tm / 60;
                                e.getMessage().getChannel().sendMessage(u.getAsMention() + " Uptime: `" + (th / 24 + "d " + th % 24 + "h " + tm % 60 + "m " + ts % 60 + "s` - `") + Math.abs(e.getMessage().getTime().until(OffsetDateTime.now(), ChronoUnit.MILLIS))  + "ms`");
                                break;
                            } case "whois": {
                                User wu = e.getMessage().getMentionedUsers().get(1);
                                ArrayList<String> c = new ArrayList<>();
                                c.add("**Here's the information on** " + wu.getAsMention());
                                c.add("```");
                                c.add("ID: " + wu.getId());
                                c.add("Name: " + wu.getUsername());
                                c.add("Discriminator: " + wu.getDiscriminator());
                                c.add("Status: " + wu.getOnlineStatus());
                                c.add("Game: " + (wu.getCurrentGame() != null ? wu.getCurrentGame().getName() : "None"));
                                c.add("Bot: " + wu.isBot());
                                c.add("Joined Discord: " + MiscUtil.getCreationTime(wu.getId()).format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
                                c.add("Joined Skript-Chat: " + e.getGuild().getJoinDateForUser(wu).format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")));
                                c.add("Roles: " + e.getGuild().getRolesForUser(wu).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)));
                                c.add("```");
                                e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                break;
                            } case "suggest": {
                                jda.getUserById("138441986314207232").getPrivateChannel().sendMessage("**Suggestion from**: " + u.getAsMention() + "\n\n" + (umsg.replace(msg[1] + " ", "")) + "");
                                e.getMessage().getChannel().sendMessage("Your suggestion has been noted " + u.getAsMention());
                                break;
                            } case "skunity": {
                                e.getMessage().getChannel().sendMessage("**Here's your link:** " + u.getAsMention() + "\n<http://skunity.com/search?search=" + (umsg.replace(msg[1] + " ", "").replaceAll(" ", "+")) + "#>");
                                break;
                            } case "sku-status": {
                                HttpURLConnection.setFollowRedirects(false);
                                HttpURLConnection c = (HttpURLConnection) new URL("https://forums.skunity.com/").openConnection();
                                c.setRequestMethod("HEAD");
                                int r = c.getResponseCode();
                                c.disconnect();
                                if (r == 403 || r == HttpURLConnection.HTTP_OK){
                                    e.getMessage().getChannel().sendMessage("**skUnity is currently:** `Up` `" + r + "`");
                                } else {
                                    e.getMessage().getChannel().sendMessage("**skUnity is currently:** `Down` `" + r + "`");
                                }
                                break;
                            } case "links": {
                                ArrayList<String> c = new ArrayList<>();
                                c.add("**Here's some links!**");
                                c.add("Skript (bensku): <https://github.com/bensku/Skript/releases>");
                                c.add("skQuery (VirusTotal): <https://github.com/SkriptLegacy/skquery/releases>");
                                c.add("Formatting: <https://support.discordapp.com/hc/en-us/articles/210298617>");
                                e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                break;
                            } case "joinlink": {
                                ArrayList<String> c = new ArrayList<>();
                                c.add("**Here's the invites for Skript-Chat!**");
                                for (InviteUtil.AdvancedInvite s : e.getGuild().getInvites()) {
                                    String chm = null;
                                    for (TextChannel ch : e.getGuild().getTextChannels()) {
                                        if (ch.getName().equals(s.getChannelName())) chm = ch.getAsMention();
                                    }
                                    c.add("  <https://discord.gg/" + s.getCode() + "> - " + chm + " (" + s.getUses() + ")");
                                }
                                e.getMessage().getChannel().sendMessage(msgBuilder(c));
                                break;
                            } case "convert": {
                                switch (msg[2]) {
                                    case "bin2txt": {
                                        String cmsg = umsg.replaceFirst(msg[1] + " ", "").replaceFirst(msg[2] + " ", "");
                                        String br = getBin2Txt(cmsg);
                                        if (br.equals("ERROR")) {
                                            e.getMessage().getChannel().sendMessage("ERROR: \n`Binary Strings can only contain 1's, 0's or spaces!`");
                                        } else {
                                            e.getMessage().getChannel().sendMessage("Binary to Text: \n```" + br + "```");
                                        }
                                        break;
                                    } case "txt2bin": {
                                        String cmsg = umsg.replaceFirst(msg[1] + " ", "").replaceFirst(msg[2] + " ", "");
                                        e.getMessage().getChannel().sendMessage("Text to Binary: \n```" + getTxt2Bin(cmsg) + "```");
                                        break;
                                    }
                                }
                                break;
                            } case "setgame": {
                                e.getMessage().deleteMessage();
                                if (e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                                    jda.getAccountManager().setGame(umsg.replaceFirst(msg[1] + " ", ""));
                                }
                                break;
                            } case "prune": {
                                e.getMessage().deleteMessage();
                                if (e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                                    Integer i = Integer.parseInt(msg[2]);
                                    if (i >= 0 && i <= 50) {
                                        for (Message s : e.getChannel().getHistory().retrieve(i)) {
                                            e.getChannel().deleteMessageById(s.getId());
                                        }
                                    }
                                }
                                break;
                            } case "kick": {
                                e.getMessage().deleteMessage();
                                if (e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                                    if (msg[1].contains("@")) {
                                        new GuildManager(e.getGuild()).kick(msg[1] + " ");
                                        e.getMessage().getChannel().sendMessage("Kicked: " + e.getMessage().getMentionedUsers().get(1));
                                    }
                                }
                                break;
                            } case "say": {
                                e.getMessage().deleteMessage();
                                if (e.getGuild().getRolesForUser(u).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                                    String sc1 = umsg.replaceFirst(msg[1] + " ", "").replace("@everyone", "").replace("@here", "");
                                    for (User tu : e.getMessage().getMentionedUsers()) {
                                        sc1 = sc1.replace("@" + tu.getUsername(), tu.getAsMention());
                                    }
                                    e.getMessage().getChannel().sendMessage(sc1);
                                }
                                break;
                            }
                        }
                    } catch (Exception x) {
                        prSysE("Exception: " + x.getMessage());
                        x.printStackTrace();
                    }
                }
            }
        }
        public void onMessageUpdate(MessageUpdateEvent e) {
            if (e.getChannel().getId().equals("237960698854899713")) {
                if (!e.getGuild().getRolesForUser(e.getMessage().getAuthor()).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
                    if (!tf.matcher(e.getMessage().getContent().toLowerCase()).find()) e.getMessage().deleteMessage();
                }
            }
        }

        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent e) {
            jda.getTextChannelById("138464183946575874").sendMessage("Welcome " + e.getUser().getAsMention() + " to Skript-Chat!");
            prSysI("@" + e.getUser().getUsername() + " has joined Skript-Chat!");
        }
        @Override
        public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
            prSysI("@" + e.getUser().getUsername() + " has left Skript-Chat!");
        }
    }

    private static String msgBuilder(ArrayList<String> s) {
        String f = "";
        for (String j : s) {
            f += ("\n" + j);
        }
        return f;
    }

    private static String getTxt2Bin(String s) {
        byte[] by = s.getBytes();
        StringBuilder bin = new StringBuilder();
        for (byte b : by) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                bin.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            bin.append(' ');
        }
        return bin.toString();
    }
    private static String getBin2Txt(String s) {
        String binV = s.trim();
        for (char character : binV.toCharArray()) {
            if (character != '0' && character != '1' && character != ' ') {
                return "ERROR";
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String sc : s.split(" ")) {
            sb.append((char) Integer.parseInt(sc, 2));
        }
        return sb.toString();
    }
}
