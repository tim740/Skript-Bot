package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.dv8tion.jda.core.Permission.MANAGE_CHANNEL;
import static net.dv8tion.jda.core.Permission.MANAGE_WEBHOOKS;
import static net.dv8tion.jda.core.Permission.MESSAGE_MANAGE;
import static uk.tim740.SkriptBot.SkriptBot.*;

/**
 * Created by tim740 on 20/09/2016
 */
class CmdSys {
  private static Pattern tf = Pattern.compile("^(?:true|false),? +the +person +below +me +.+$");
  private static Color dc = Color.decode("#2D9CE2");
  private static ArrayList<String> cmds = new ArrayList<>();
  private static ArrayList<String> cmdargs = new ArrayList<>();
  private static ArrayList<String> cmdDes = new ArrayList<>();
  private static ArrayList<String> cmdRank = new ArrayList<>();
  
  static void reg() {
    cmds.add("help");
    cmdargs.add("");
    cmdDes.add("Help Command");
    cmdRank.add("user");

    cmds.add("info");
    cmdargs.add("");
    cmdDes.add("Chat Info.");
    cmdRank.add("user");

    cmds.add("bots");
    cmdargs.add("");
    cmdDes.add("Bots in Skript-Chat.");
    cmdRank.add("user");

    cmds.add("aliases");
    cmdargs.add("");
    cmdDes.add("Aliases version.");
    cmdRank.add("user");

    cmds.add("whois");
    cmdargs.add("%user%");
    cmdDes.add("User Lookup.");
    cmdRank.add("user");

    cmds.add("skunity");
    cmdargs.add("%string%");
    cmdDes.add("Lookup on skUnity Docs.");
    cmdRank.add("user");

    cmds.add("sku-status");
    cmdargs.add("");
    cmdDes.add("Checks if skUnity is up.");
    cmdRank.add("user");

    cmds.add("links");
    cmdargs.add("");
    cmdDes.add("Useful Links.");
    cmdRank.add("user");

    cmds.add("invites");
    cmdargs.add("");
    cmdDes.add("Invites for Skript-Chat.");
    cmdRank.add("user");

    cmds.add("bin2txt");
    cmdargs.add("%binary%");
    cmdDes.add("Convert binary to text.");
    cmdRank.add("user");

    cmds.add("txt2bin");
    cmdargs.add("%string%");
    cmdDes.add("Convert text to binary.");
    cmdRank.add("user");

    cmds.add("embed");
    cmdargs.add("%json string%");
    cmdDes.add("Generates a Embed.");
    cmdRank.add("user");

    cmds.add("stats");
    cmdargs.add("");
    cmdDes.add("Returns Bot Stats.");
    cmdRank.add("user");

    cmds.add("request-addon");
    cmdargs.add("%name% %link%");
    cmdDes.add("Request a Channel for your addon.");
    cmdRank.add("user");

    cmds.add("reg-addon");
    cmdargs.add("%name% %author% %link%");
    cmdDes.add("Create an addon channel.");
    cmdRank.add("admin");

    cmds.add("purge");
    cmdargs.add("%number%");
    cmdDes.add("Remove (1-100) msgs.");
    cmdRank.add("admin");

    cmds.add("say");
    cmdargs.add("%string%");
    cmdDes.add("Speak as the Bot.");
    cmdRank.add("admin");
  }

  static void cmdSys(String tk) {
    try {
      jda = new JDABuilder(AccountType.BOT).setToken(tk).addEventListener(new MessageListener()).buildBlocking();
    } catch (Exception x) {
      System.exit(0);
    }
  }

  private static void cmd(String[] args, String gi, Message m) {
    String umsg = m.getContent().replaceFirst(args[0] + " ", "").replaceFirst("@Skript-Bot", "");
    User u = m.getAuthor();
    Guild g = jda.getGuildById((!Objects.equals(gi, "") ? gi : "138464183946575874"));
    try {
      switch (args[0].toLowerCase()) {
        case "help": {
          if (!u.hasPrivateChannel()) {
            u.openPrivateChannel().complete();
          }
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setTitle("**COMMANDS** (All Commands start with `@Skript-Bot`)", "https://tim740.github.io");
          for (int i = 0; i < cmds.size(); i++) {
            if (cmdRank.get(i).equals("user")) {
              eb.addField(cmds.get(i) + " " + cmdargs.get(i), cmdDes.get(i), true);
            } else if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
              eb.addField(cmds.get(i) + " " + cmdargs.get(i), cmdDes.get(i), true);
            }
          }
          u.getPrivateChannel().sendMessage(eb.build()).queue();
          m.addReaction("\uD83D\uDC4D").queue();
          break;
        } case "info": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          int on = 0, off = 0, bot = 0;
          for (Member s : g.getMembers()) {
            if (s.getOnlineStatus().equals(OnlineStatus.ONLINE) || s.getOnlineStatus().equals(OnlineStatus.IDLE) || s.getOnlineStatus().equals(OnlineStatus.DO_NOT_DISTURB)) {
              on++;
            } else if (s.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
              off++;
            }
            if (s.getUser().isBot()) {
              bot++;
            }
          }
          eb.setTitle("**Here's the information in " + g.getName() + "**", g.getIconUrl());
          eb.addField("Online Users:", (on + "/" + g.getMembers().size()), true);
          eb.addField("Offline Users:", (off + "/" + g.getMembers().size()), true);
          eb.addField("Bots:", (bot + "/" + g.getMembers().size()), true);
          eb.addField("Text/Voice Channels:", (g.getTextChannels().size() + "/" + g.getVoiceChannels().size()), true);
          eb.setFooter("Creator: " + g.getOwner().getEffectiveName(), g.getOwner().getUser().getAvatarUrl());
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "bots": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          int on = 0, off = 0;
          String bots = "";
          for (Member s : g.getMembers()) {
            if (s.getUser().isBot()) {
              bots += "@" + s.getUser().getName() + ":" + s.getUser().getDiscriminator() + ", ";
              if (s.getOnlineStatus().equals(OnlineStatus.ONLINE) || s.getOnlineStatus().equals(OnlineStatus.IDLE)) {
                on++;
              } else if (s.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                off++;
              }
            }
          }
          eb.setTitle("**Here's the bot information in " + g.getName() + "**", "https://tim740.github.io");
          eb.setDescription(bots);
          eb.addField("Online Bots:", (on + "/" + (on + off)), true);
          eb.addField("Offline Bots:", (off + "/" + (on + off)), true);
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "aliases": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          BufferedReader ur = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/tim740/skAliases/master/version.txt").openStream()));
          eb.addField("Latest aliases version:", ("v" + ur.readLine()) + " <https://forums.skunity.com/resources/aliases.27/>", false);
          ur.close();
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "whois": {
          Member wu = g.getMember(m.getMentionedUsers().get(1));
          User mu = m.getMentionedUsers().get(1);
          EmbedBuilder eb = new EmbedBuilder();
          if (wu.getOnlineStatus() == OnlineStatus.ONLINE) {
            eb.setColor(Color.decode("#21D66F"));
          } else if (wu.getOnlineStatus() == OnlineStatus.IDLE) {
            eb.setColor(Color.decode("#E97A18"));
          } else if (wu.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB) {
            eb.setColor(Color.decode("#EF493A"));
          }
          eb.setAuthor("@" + mu.getName() + "#" + mu.getDiscriminator() + " - (" + wu.getEffectiveName() + ")", mu.getAvatarUrl(), mu.getAvatarUrl());
          eb.addField("ID:", mu.getId(), true);
          eb.addField("Game:", (wu.getGame() != null ? wu.getGame().getName() : "None"), true);
          eb.addField("Joined Discord:", mu.getCreationTime().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")), true);
          eb.addField("Joined Skript-Chat:", g.getMember(mu).getJoinDate().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")), true);
          eb.addField("Roles:", String.valueOf(g.getMember(mu).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new))), true);
          eb.setFooter(g.getName(), g.getIconUrl());
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "skunity": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setAuthor(u.getName(), u.getAvatarUrl(), u.getAvatarUrl());
          eb.addField("Link:", "<http://skUnity.com/search?search=" + (umsg.replaceAll(" ", "+")) + "#>", true);
          eb.setFooter("Result from skUnity.com", "https://www.skunity.com/favicon.ico");
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "sku-status": {
          m.getChannel().sendMessage(cOs("https://www.skunity.com", "skUnity Docs", "https://www.skunity.com/favicon.ico")).queue();
          m.getChannel().sendMessage(cOs("https://forums.skunity.com", "skUnity Forums", "https://forums.skunity.com/favicon.ico")).queue();
          break;
        } case "embed": {
          if (args[1].contains("{")) {
            JSONObject j = (JSONObject) new JSONParser().parse(umsg);
            EmbedBuilder eb = new EmbedBuilder();
            if (j.containsKey("author")) {
              JSONObject jo = (JSONObject) j.get("author");
              eb.setAuthor((String) jo.get("content"), (String) jo.get("linkurl"), (String) jo.get("iconurl"));
            }
            if (j.containsKey("color")) {
              eb.setColor(Color.decode((String) j.get("color")));
            }
            if (j.containsKey("title")) {
              eb.setTitle((String) j.get("title"), "https://tim740.github.io");
            }
            if (j.containsKey("desc")) {
              eb.setDescription(((String) j.get("desc")).replaceAll("%nl%", System.lineSeparator()));
            }
            String in = j.toJSONString();
            int id = in.indexOf("field");
            int c = 0;
            while (id != -1) {
              c++;
              in = in.substring(id + 1);
              id = in.indexOf("field");
            }
            for (int n = 0; n < c; n++) {
              JSONObject jo = (JSONObject) j.get("field" + n);
              eb.addField((String) jo.get("name"), ((String) jo.get("content")).replaceAll("%nl%", System.lineSeparator()), Boolean.TRUE.equals(jo.get("inline")));
            }
            if (j.containsKey("footer")) {
              JSONObject jo = (JSONObject) j.get("footer");
              eb.setFooter((String) jo.get("content"), (String) jo.get("iconurl"));
            }
            m.getChannel().sendMessage(eb.build()).queue();
          } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(dc);
            eb.setTitle("**Embed Help (Flags)**", "https://tim740.github.io");
            eb.addField("Author:", "```json\n\"author\":{\"content\":\"%text%\",\"linkurl\":\"%url%\",\"iconurl\":\"%url%\"}```", false);
            eb.addField("Color:", "```json\n\"color\":\"#hexColor%\"```", false);
            eb.addField("Title:", "```json\n\"title\":\"%text%\"```", false);
            eb.addField("Description:", "```json\n\"desc\":\"%text%\"```", false);
            eb.addField("Field:", "```json\n\"field%int%\":{\"name\":\"%text%\",\"content\":\"%text%\",\"inline\":%boolean%}```", false);
            eb.addField("Footer:", "```json\n\"footer\":{\"content\":\"%text%\",\"iconurl\":\"%url%\"}```", false);
            eb.addField("Example:", "```json\n@Skript-Bot embed {\"author\":{\"content\":\"text\",\"linkurl\":\"https://www.google.com\",\"iconurl\":\"https://www.google.com/favicon.ico\"},\"color\":\"#FFFFFF\",\"desc\":\"qqqqq\",\"title\":\"title\",\"field0\":{\"name\":\"text\",\"content\":\"text\",\"inline\":false},\"footer\":{\"content\":\"text\",\"iconurl\":\"https://www.google.com/favicon.ico\"}}```", true);
            m.getChannel().sendMessage(eb.build()).queue();
          }
          break;
        } case "links": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.addField("Skript:", "[Aliases](https://github.com/tim740/skAliases/releases/latest) - [Bensku](https://github.com/bensku/Skript/releases) - [Mirre](https://github.com/Mirreski/Skript/wiki) - [Nfell](http://nfell2009.uk/skript/downloads)", false);
          eb.addField("skQuery:", "[VirusTotal](https://github.com/SkriptLegacy/skquery/releases)", false);
          eb.addField("Formatting:", "<https://support.discordapp.com/hc/en-us/articles/210298617>", false);
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "invites":{
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setAuthor(jda.getGuildById(skcid).getName() + " - Invites.", jda.getGuildById(skcid).getIconUrl(), jda.getGuildById(skcid).getIconUrl());
          String desc = "";
          for (Invite i : jda.getGuildById(skcid).getInvites().submit().get()){
            desc += (", [#" + i.getChannel().getName() + "](https://discord.gg/" + i.getCode() + ") (" + i.getUses() + ")");
          }
          eb.setDescription(desc.replaceFirst(",", ""));
          m.getChannel().sendMessage(eb.build()).queue();

          EmbedBuilder eb1 = new EmbedBuilder();
          eb1.setColor(dc);
          eb1.setAuthor(jda.getGuildById(skcaid).getName() + " - Invites.", jda.getGuildById(skcaid).getIconUrl(), jda.getGuildById(skcaid).getIconUrl());
          String desc1 = "";
          for (Invite i : jda.getGuildById(skcaid).getInvites().submit().get()){
            desc1 += (", [#" + i.getChannel().getName() + "](https://discord.gg/" + i.getCode() + ") (" + i.getUses() + ")");
          }
          eb1.setDescription(desc1.replaceFirst(",", ""));
          m.getChannel().sendMessage(eb1.build()).queue();
          break;
        } case "bin2txt": case "txt2bin": {
          long ct = System.currentTimeMillis();
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          switch (args[0]) {
            case "bin2txt": {
              String br = getBin2Txt(umsg);
              eb.setTitle("**Binary to Text**", "https://tim740.github.io");
              if (br.equals("ERROR")) {
                eb.addField("Error:", "Binary Strings can only contain 1's, 0's or spaces!", false);
                eb.addField("Input:", "```" + umsg + "```", false);
              } else {
                eb.addField("Input:", "```" + umsg + "```", false);
                eb.addField("Output:", "```" + br + "```", false);
              }
              break;
            } case "txt2bin": {
              eb.setTitle("**Text to Binary**", "https://tim740.github.io");
              eb.addField("Input:", "```" + umsg + "```", false);
              eb.addField("Output:", "```" + getTxt2Bin(umsg) + "```", false);
              break;
            }
          }
          eb.setFooter("Processed in " + (System.currentTimeMillis() - ct) + "ms", u.getAvatarUrl());
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "request-addon": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setTitle("Addon Channel Request");
          eb.addField("Command:", "`@Skript-Bot reg-addon " + args[1] + " @" + m.getAuthor().getName() + " " + args[2] + "`", false);
          jda.getGuildById(skcid).getTextChannelsByName("staff", false).get(0).sendMessage(eb.build()).queue();
          break;
        } case "reg-addon": {
          if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
            for (TextChannel tci: jda.getGuildById(skcaid).getTextChannels()) {
              if (tci.getName().equals(args[1])) {
                jda.getGuildById(skcid).getTextChannelsByName("staff", false).get(0).sendMessage("**Addon Channel:** `#" + args[1] + "` already exists!").queue();
                break;
              }
            }
            Member gm = jda.getGuildById(skcaid).getMemberById(m.getMentionedUsers().get(1).getId());
            if (!gm.getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Addon Dev")) {
              jda.getGuildById(skcaid).getController().addRolesToMember(gm, jda.getGuildById(skcaid).getRoleById("252875979477745665")).queue();
            }
            if (!jda.getGuildById(skcid).getMember(gm.getUser()).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Addon Dev")) {
              jda.getGuildById(skcid).getController().addRolesToMember(jda.getGuildById(skcid).getMemberById(m.getMentionedUsers().get(1).getId()), g.getRoleById("138470986809999360")).queue();
            }
            jda.getGuildById(skcaid).getController().createTextChannel(args[1]).setTopic(args[1] + " related stuff: " + args[3]).queue(grac -> {
              grac.createPermissionOverride(gm).setAllow(MANAGE_WEBHOOKS, MANAGE_CHANNEL, MESSAGE_MANAGE).queue();
              grac.createInvite().setTemporary(false).queue();
              ((TextChannel)grac).sendMessage("This is a Temporary message please remove this after you have read and understand it.\n" +
                  "This is your channel for related chat about your addon, you can manage this channel, change the topic, create WebHooks, and remove messages, if this is abused this permission can be removed!\n" +
                  "You also now have access to #addon-updates (In Skript-Chat) where you can post updates for your addon, please make sure to stick to the format, and only use this channel for posting addon or tool updates.\n" +
                  "Don't know what a WebHook is? You can create a WebHook that allows you to link this channel and your addon's Github repo so when you get an issue, or commit on github it will be posted in this channel.\n" +
                  "If you have any other questions ask a member of Staff!").queue();
            });
            jda.getGuildById(skcid).getTextChannelsByName("staff", false).get(0).sendMessage("**Addon Channel:** `#" + args[1] + "` has been created!").queue();
          }
          break;
        } case "purge": {
          m.delete();
          if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
            Integer i = Integer.parseInt(args[1]);
            if (i >= 0 && i <= 100) {
              ((TextChannel) m.getChannel()).deleteMessages(m.getChannel().getHistory().retrievePast(i).complete()).complete();
            }
          }
          break;
        } case "say": {
          if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
            m.delete().queue();
            String sc1 = umsg.replace("@everyone", "").replace("@here", "");
            for (User tu : m.getMentionedUsers()) {
              sc1 = sc1.replace("@" + tu.getName(), tu.getAsMention());
            }
            m.getChannel().sendMessage(sc1).queue();
          }
          break;
        } case "stats": {
          long ts = (System.currentTimeMillis() - st) / 1000;
          long tm = ts / 60;
          long th = tm / 60;
          BufferedReader ur = new BufferedReader(new InputStreamReader(new URL("https://api.github.com/repos/tim740/Skript-Bot/commits").openStream()));
          String[] s = ur.lines().toArray(String[]::new);
          ur.close();
          JSONArray j = (JSONArray) new JSONParser().parse(s[0]);
          JSONObject jo = (JSONObject) j.get(0);
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setAuthor("tim740 (18/09/2016)", "https://tim740.github.io", g.getMemberById("138441986314207232").getUser().getAvatarUrl());
          eb.addField("Source:", "[GitHub](https://github.com/tim740/Skript-Bot) - [" + jo.get("sha").toString().substring(0, 7) + "](" + "https://github.com/tim740/Skript-Bot/commit/" + jo.get("sha").toString() + ")", true);
          eb.addField("Library:", "[JDA](https://github.com/DV8FromTheWorld/JDA) " + JDAInfo.VERSION, true);
          eb.addField("Commands:", "`@Skript-Bot help` - " + cmds.size(), true);
          eb.addField("Ram (Used/Total):", ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000000) + "/" + (Runtime.getRuntime().totalMemory() / 1000000) + "MB", true);
          eb.addField("Ping:", Math.abs(m.getCreationTime().until(OffsetDateTime.now(), ChronoUnit.MILLIS))  + "ms", true);
          eb.addField("Uptime:", (th / 24 + "d " + th % 24 + "h " + tm % 60 + "m " + ts % 60 + "s"), true);
          eb.setFooter(jo.get("sha").toString().substring(0, 7) + " - " + ((JSONObject) jo.get("commit")).get("message"), "https://github.com/favicon.ico");
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        }
      }
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  private static class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
      cmdEx(e.getGuild(), e.getMessage());
    }
    public void onMessageUpdate(MessageUpdateEvent e) {
      cmdEx(e.getGuild(), e.getMessage());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
      prSysI(e.getGuild(), e.getMember().getUser(), "joined!");
      if (e.getGuild().getId().equals(skcid)) {
        jda.getTextChannelById("138464183946575874").sendMessage("Welcome " + e.getMember().getAsMention() + " to Skript-Chat!").queue();
      }
    }
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        prSysI(e.getGuild(), e.getMember().getUser(), "left!");
    }
    @Override
    public void onGuildBan(GuildBanEvent e) {
      EmbedBuilder eb = new EmbedBuilder();
      eb.setColor(Color.decode("#EF493A"));
      eb.setAuthor("@" + e.getUser().getName() + "#" + e.getUser().getDiscriminator() + " (" + e.getGuild().getMember(e.getUser()).getEffectiveName() + ")", e.getUser().getAvatarUrl(), e.getUser().getAvatarUrl());
      eb.setDescription("Banned!");
      eb.setFooter(e.getGuild().getName(), e.getGuild().getIconUrl());
      jda.getGuildById(skcid).getTextChannelsByName("staff", false).get(0).sendMessage(eb.build()).queue();
    }
  }

  private static void cmdEx(Guild g, Message m) {
    if (!m.getAuthor().getId().equals("227067574469394432")) {
      if (!m.isFromType(ChannelType.PRIVATE)) {
        if (!m.getChannel().getId().equals(lcid)) {
          if (m.getChannel().getId().equals("237960698854899713")) {
            if (!g.getMember(m.getAuthor()).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
              if (!tf.matcher(m.getContent().toLowerCase()).find()) m.delete().queue();
            }
          } else if (m.getContent().toLowerCase().startsWith("@skript-bot")) {
            if (validCmd(m.getContent().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "").split(" ")).equals(true)) {
              prSysI(g, (TextChannel) m.getChannel(), m.getAuthor(), "executed: '" + m.getContent().replaceFirst("@Skript-Bot ", "") + "'");
              cmd(m.getContent().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "").split(" "), g.getId(), m);
            }
          }
        } else {
          m.delete().queue();
        }
      } else {
        if (validCmd(m.getContent().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "").split(" ")).equals(true)) {
          prSysI(m.getAuthor(), "executed: '" + m.getContent().replaceFirst("<@227067574469394432> ", "") + "'");
          cmd(m.getContent().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "").split(" "), "", m);
        }
      }
    }
  }
  private static Boolean validCmd(String[] args) {
    return (cmds.contains(args[0].toLowerCase()));
  }

  private static MessageEmbed cOs(String u, String n, String ico) throws Exception {
    HttpURLConnection c = (HttpURLConnection) new URL(u).openConnection();
    c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
    int r = c.getResponseCode();
    c.disconnect();
    EmbedBuilder eb = new EmbedBuilder().setAuthor(n, u, ico);
    if (r == HttpURLConnection.HTTP_OK){
      eb.setDescription("**Online**: `" + r + "`").setColor(Color.decode("#21D66F"));
    } else {
      eb.setDescription("**Offline**: `" + r + "`").setColor(Color.decode("#EF493A"));
    }
    return eb.build();
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
      if (character != '0' && character != '1' && character != ' ') return "ERROR";
    }
    StringBuilder sb = new StringBuilder();
    for (String sc : s.split(" ")) {
      sb.append((char) Integer.parseInt(sc, 2));
    }
    return sb.toString();
  }
}
