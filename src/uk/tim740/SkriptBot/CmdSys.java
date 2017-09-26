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
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
  private Pattern tf = Pattern.compile("^(?:true|false),? +the +person +below +me +.+$");
  private Color dc = Color.decode("#2D9CE2");
  final String SKC_ID = "138464183946575874";
  final String LC_ID = "327617436713091072";
  private final String MC_ID = "138464183946575874";
  private final String SC_ID = "139843895063347201";
  private ArrayList<String> cmds = new ArrayList<>();
  private ArrayList<String> cargs = new ArrayList<>();
  private ArrayList<String> cdesc = new ArrayList<>();
  private ArrayList<String> crank = new ArrayList<>();
  
  void reg(String tk) {
    try {
      jda = new JDABuilder(AccountType.BOT).setToken(tk).addEventListener(new MessageListener()).buildBlocking();
    } catch (Exception x) {
      System.exit(0);
    }
    cmdBuilder("help", "", "Help Command", "user");
    cmdBuilder("info", "" , "Chat Info", "user");
    cmdBuilder("bots", "", "Bots in Skript-Chat", "user");
    cmdBuilder("whois", "%user%", "User Lookup", "user");
    cmdBuilder("sku-status", "", "Checks if skUnity is Up", "user");
    cmdBuilder("invites", "", "Invites for Skript-Chat", "user");
    cmdBuilder("embed", "help | %json string%", "Generates a Embed", "user");
    cmdBuilder("stats", "", "Returns Bot Stats", "user");
    cmdBuilder("request-addon", "%name% %link%", "Request a Addon Channel", "user");
    cmdBuilder("warn", "%name% %reason%", "Warn a User", "admin");
    cmdBuilder("reg-addon", "%name% %author% %link%", "Create an Addon Channel", "admin");
    cmdBuilder("purge", "%number%", "Remove 1-100 msgs", "admin");
  }

  private void cmdBuilder(String cmd, String args, String desc, String rank) {
    cmds.add(cmd); cargs.add(args); cdesc.add(desc); crank.add(rank);
  }

  private void cmd(String[] args, String gi, Message m) {
    String umsg = m.getContent().replaceFirst(args[0] + " ", "").replaceFirst("@Skript-Bot", "");
    User u = m.getAuthor();
    Guild g = jda.getGuildById((!Objects.equals(gi, "") ? gi : MC_ID));
    try {
      switch (args[0].toLowerCase()) {
        case "help": {
          if (!u.hasPrivateChannel()) {
            u.openPrivateChannel().complete();
          }
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setTitle("**COMMANDS** (All Commands start with `@Skript-Bot`)");
          for (int i = 0; i < cmds.size(); i++) {
            if (crank.get(i).equals("user")) {
              eb.addField(cmds.get(i) + " " + cargs.get(i), cdesc.get(i), true);
            } else if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
              eb.addField(cmds.get(i) + " " + cargs.get(i), cdesc.get(i), true);
            }
          }
          u.openPrivateChannel().queue(c -> c.sendMessage(eb.build()).queue());
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
          eb.setTitle("**Here's the information in " + g.getName() + "**");
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
          StringBuilder bots = new StringBuilder();
          for (Member s : g.getMembers()) {
            if (s.getUser().isBot()) {
              bots.append("@").append(s.getUser().getName()).append(":").append(s.getUser().getDiscriminator()).append(", ");
              if (s.getOnlineStatus().equals(OnlineStatus.ONLINE) || s.getOnlineStatus().equals(OnlineStatus.IDLE)) {
                on++;
              } else if (s.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
                off++;
              }
            }
          }
          eb.setTitle("**Here's the bot information in " + g.getName() + "**");
          eb.setDescription(bots.toString());
          eb.addField("Online Bots:", (on + "/" + (on + off)), true);
          eb.addField("Offline Bots:", (off + "/" + (on + off)), true);
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
        } case "sku-status": {
          m.getChannel().sendMessage(cOs("https://www.skunity.com", "skUnity Docs", "https://www.skunity.com/favicon.ico")).queue();
          m.getChannel().sendMessage(cOs("https://forums.skunity.com", "skUnity Forums", "https://forums.skunity.com/favicon.ico")).queue();
          break;
        } case "embed": {
          if (args[1].contains("{")) {
            if (umsg.contains("|&|")) {
              ArrayList<String> umsgl = new ArrayList<>();
              Collections.addAll(umsgl, umsg.split("\\|&\\|"));
              for (String cUmsgl : umsgl) {
                m.getChannel().sendMessage(embedBuilder(cUmsgl)).queue();
              }
            } else {
              m.getChannel().sendMessage(embedBuilder(umsg)).queue();
            }
          } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(dc);
            eb.setTitle("**Embed Help (Flags)**");
            eb.addField("Author:", "```json\n\"author\":{\"content\":\"%text%\",\"linkurl\":\"%url%\",\"iconurl\":\"%url%\"}```", false);
            eb.addField("Color:", "```json\n\"color\":\"#hexColor%\"```", false);
            eb.addField("Title:", "```json\n\"title\":{\"title\":\"%text%\",\"titleurl\":\"%optional-url%\"}```", false);
            eb.addField("Description:", "```json\n\"desc\":\"%text%\"```", false);
            eb.addField("Field:", "```json\n\"field%int%\":{\"name\":\"%text%\",\"content\":\"%text%\",\"inline\":%boolean%}```", false);
            eb.addField("Footer:", "```json\n\"footer\":{\"content\":\"%text%\",\"iconurl\":\"%url%\"}```", false);
            eb.addField("Example:", "```json\n@Skript-Bot embed {\"author\":{\"content\":\"text\",\"linkurl\":\"https://www.google.com\",\"iconurl\":\"https://www.google.com/favicon.ico\"},\"color\":\"#FFFFFF\",\"desc\":\"qqqqq\",\"title\":{\"title\":\"title\"},\"field0\":{\"name\":\"text\",\"content\":\"text\",\"inline\":false},\"footer\":{\"content\":\"text\",\"iconurl\":\"https://www.google.com/favicon.ico\"}}```", true);
            eb.addField("Keys:", "**New Line:** `%nl%` - **Multi Embed:** `|&|` (At the start of each new Embed)", false);
            m.getChannel().sendMessage(eb.build()).queue();
          }
          break;
        } case "invites":{
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setAuthor(jda.getGuildById(SKC_ID).getName() + " - Invites.", jda.getGuildById(SKC_ID).getIconUrl(), jda.getGuildById(SKC_ID).getIconUrl());
          eb.setDescription(invBuilder(jda.getGuildById(SKC_ID)));
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "request-addon": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setTitle("Addon Channel Request: " + args[1]);
          eb.addField("Command:", "`@Skript-Bot reg-addon " + args[1] + " @" + m.getAuthor().getName() + " " + args[2] + "`", false);
          jda.getGuildById(SKC_ID).getTextChannelById(SC_ID).sendMessage(eb.build()).queue();
          m.addReaction("\uD83D\uDC4D").queue();
          break;
        } case "reg-addon": {
          if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
            for (TextChannel tci : jda.getGuildById(SKC_ID).getTextChannels()) {
              if (tci.getName().equals(args[1])) {
                jda.getGuildById(SKC_ID).getTextChannelById(SC_ID).sendMessage("**Addon Channel:** `#" + args[1] + "` already exists!").queue();
              }
            }
            Member gm = g.getMemberById(m.getMentionedUsers().get(1).getId());
            if (!g.getMember(gm.getUser()).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Addon Dev")) {
              g.getController().addRolesToMember(g.getMemberById(m.getMentionedUsers().get(1).getId()), g.getRoleById("138470986809999360")).queue();
            }
            g.getController().createTextChannel(args[1]).setTopic("v0.0.0 | Forums: " + args[3] + "|\n\nOnly use this channel for " + args[1] + " related chat.").queue(grac -> {
              grac.createPermissionOverride(gm).setAllow(MANAGE_WEBHOOKS, MANAGE_CHANNEL, MESSAGE_MANAGE).queue();
              grac.createInvite().setTemporary(false).queue();
              //grac.getGuild().getController().modifyTextChannelPositions().
              ((TextChannel) grac).sendMessage(gm.getAsMention() + " This is a Temporary message please remove this after you have read and understand it.\n" +
                  "This is your channel for related chat about your addon, you can manage this channel, change the topic, create WebHooks, and remove messages, if this is abused this permission can be removed!\n" +
                  "You also now have access to #addon-updates where you can post updates for your addon, please make sure to stick to the format, and only use this channel for posting addon or tool updates.\n" +
                  "Don't know what a WebHook is? You can create a WebHook that allows you to link this channel and your addon's Github repo so when you get an issue, or commit on github it will be posted in this channel, Help link: https://support.discordapp.com/hc/en-us/articles/228383668-Intro-to-Webhooks\n" +
                  "If you have any other questions ask a member of Staff!").queue();
            });
            g.getTextChannelById(SC_ID).sendMessage("**Addon Channel:** `#" + args[1] + "` has been created!").queue();
            jda.getTextChannelById(MC_ID).sendMessage("Addon Channel: **" + args[1] + "** has just been created!").queue();
          }
          break;
        } case "warn": {
          User mu = m.getMentionedUsers().get(1);
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(Color.decode("#EF493A"));
          eb.addField("Warned: " + mu.getAsMention(), "Reason: `" + args[2] + "` (**" +  "**/**5**)", false);
          jda.getGuildById(SKC_ID).getTextChannelById(SC_ID).sendMessage(eb.build()).queue();
          jda.getGuildById(SKC_ID).getTextChannelById(MC_ID).sendMessage(eb.build()).queue();
          break;
        } case "purge": {
          if (g.getMember(u).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
            Integer i = Integer.parseInt(args[1]);
            if (i >= 0 && i <= 100) {
              ((TextChannel) m.getChannel()).deleteMessages(m.getChannel().getHistory().retrievePast(i).complete()).complete();
            }
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

  private void cmdEx(Guild g, Message m) {
    if (!m.getAuthor().getId().equals("227067574469394432")) {
      String mc = m.getContent().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "");
      if (!m.isFromType(ChannelType.PRIVATE)) {
        if (!m.getChannel().getId().equals(LC_ID)) {
          if (m.getChannel().getId().equals("237960698854899713")) {
            if (!g.getMember(m.getAuthor()).getRoles().stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new)).contains("Staff")) {
              if (!tf.matcher(m.getContent().toLowerCase()).find()) m.delete().queue();
            }
          } else if (m.getContent().toLowerCase().startsWith("@skript-bot")) {
            if (validCmd(mc.split(" ")).equals(true)) {
              prSysI(g, (TextChannel) m.getChannel(), m.getAuthor(), "executed: '" + mc + "'");
              cmd(mc.split(" "), g.getId(), m);
            }
          }
        } else {
          m.delete().queue();
        }
      } else {
        if (validCmd(mc.split(" ")).equals(true)) {
          prSysI("Private", m.getAuthor(), "executed: '" + mc + "'");
          cmd(mc.split(" "), "", m);
        }
      }
    }
  }

  private class MessageListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
      cmdEx(e.getGuild(), e.getMessage());
    }
    public void onMessageUpdate(MessageUpdateEvent e) {
      cmdEx(e.getGuild(), e.getMessage());
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
      prSysI(e.getGuild().getName(), e.getMember().getUser(), "joined!");
    }
    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
        prSysI(e.getGuild().getName(), e.getMember().getUser(), "left!");
    }
    @Override
    public void onGuildBan(GuildBanEvent e) {
      EmbedBuilder eb = new EmbedBuilder();
      eb.setColor(Color.decode("#EF493A"));
      eb.setAuthor("@" + e.getUser().getName() + "#" + e.getUser().getDiscriminator() + " (" + e.getGuild().getMember(e.getUser()).getEffectiveName() + ")", e.getUser().getAvatarUrl(), e.getUser().getAvatarUrl());
      eb.setDescription("Banned!");
      eb.setFooter(e.getGuild().getName(), e.getGuild().getIconUrl());
      jda.getGuildById(SKC_ID).getTextChannelById(SC_ID).sendMessage(eb.build()).queue();
    }
  }

  private Boolean validCmd(String[] args) {
    return cmds.contains(args[0].toLowerCase());
  }

  private MessageEmbed cOs(String u, String n, String ico) throws Exception {
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

  private MessageEmbed embedBuilder(String text) {
    EmbedBuilder eb = new EmbedBuilder();
    try {
      JSONObject j = (JSONObject) new JSONParser().parse(text);
      if (j.containsKey("author")) {
        JSONObject jo = (JSONObject) j.get("author");
        eb.setAuthor((String) jo.get("content"), (String) jo.get("linkurl"), (String) jo.get("iconurl"));
      }
      if (j.containsKey("color")) {
        eb.setColor(Color.decode((String) j.get("color")));
      }
      if (j.containsKey("title")) {
        JSONObject jo = (JSONObject) j.get("title");
        if (jo.containsKey("titleurl")) {
          eb.setTitle((String) jo.get("title"), (String) jo.get("titleurl"));
        } else {
          eb.setTitle((String) jo.get("title"));
        }
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
    } catch (ParseException x) {
      x.printStackTrace();
    }
    return eb.build();
  }

  private String invBuilder(Guild g) {
    StringBuilder desc = new StringBuilder();
    try {
      for (Invite i : g.getInvites().submit().get()) {
        desc.append(", [#").append(i.getChannel().getName()).append("](https://discord.gg/").append(i.getCode()).append(") (").append(i.getUses()).append(")");
      }
    } catch (Exception x) {
      x.printStackTrace();
    }
    return desc.toString().replaceFirst(",", "");
  }
}
