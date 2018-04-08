package uk.tim740.SkriptBot;

import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
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
  private Pattern tf = Pattern.compile("^(?:true|false),? +the +person +below +me +.+$");
  private Color dc = Color.decode("#2D9CE2");
  Guild GO = null;
  private TextChannel AC = null;
  private TextChannel SC = null;
  final String LC_ID = "327617436713091072";
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
    GO = jda.getGuildById("138464183946575874");
    AC = GO.getTextChannelById("394179773234020362");
    SC = GO.getTextChannelById("139843895063347201");
    cmdBuilder("help", "", "Help Command", "user");
    cmdBuilder("info", "" , "Chat Info", "user");
    cmdBuilder("whois", "%user%", "User Lookup", "user");
    cmdBuilder("invites", "", "Invites for Skript-Chat", "user");
    cmdBuilder("embed", "help | %json string%", "Generates a Embed", "user");
    cmdBuilder("stats", "", "Returns Bot Stats", "user");
    cmdBuilder("request-addon", "%name% %link%", "Request a Addon Channel", "user");
    cmdBuilder("warn", "%name% %reason%", "Warn a User", "admin");
    cmdBuilder("purge", "%number%", "Remove 1-100 msgs", "admin");
  }

  private void cmdBuilder(String cmd, String args, String desc, String rank) {
    cmds.add(cmd); cargs.add(args); cdesc.add(desc); crank.add(rank);
  }

  @SuppressWarnings("unchecked")
  private void cmd(String[] args, String gi, Message m) {
    String umsg = m.getContentStripped().replaceFirst(args[0] + " ", "").replaceFirst("@Skript-Bot", "");
    User u = m.getAuthor();
    Guild g = jda.getGuildById((gi == null || gi.isEmpty()) ? "138464183946575874" : gi);//MC
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
        } case "whois": {
          User mu = m.getMentionedUsers().get(1);
          Member wu = g.getMember(mu);
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
          eb.addField("Roles:", String.valueOf(g.getMember(mu).getRoles().stream().map(Role::getName).collect(Collectors.toList())), true);
          eb.setFooter(g.getName(), g.getIconUrl());
          m.getChannel().sendMessage(eb.build()).queue();
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
          eb.setAuthor(GO.getName() + " - Invites (Right Click Copy Link)", GO.getIconUrl(), GO.getIconUrl());
          eb.setDescription(invBuilder(GO));
          m.getChannel().sendMessage(eb.build()).queue();
          break;
        } case "request-addon": {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(dc);
          eb.setAuthor(m.getAuthor().getName() + " - (Addon Channel Request)", m.getAuthor().getAvatarUrl(), m.getAuthor().getAvatarUrl());
          eb.addField("Author:", m.getAuthor().getId(), false);
          eb.addField("Addon:", args[1], false);
          eb.addField("Link:", args[2], false);
          eb.setFooter("Accept this request by adding another :thumbsup:", g.getIconUrl());
          SC.sendMessage(eb.build()).complete().addReaction("\uD83D\uDC4D").queue();
          m.addReaction("\uD83D\uDC4D").queue();
          break;
        } case "warn": {
          if (g.getMember(u).getRoles().stream().filter(r -> r.getName().equals("Staff")).collect(Collectors.toList()).size() > 0) {
            User mu = m.getMentionedUsers().get(1);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.decode("#EF493A"));
            JSONObject j = (JSONObject) new JSONParser().parse(new FileReader("warning.json"));
            long warnCount = 1;
            if (j.containsKey(mu.getId())) {
              warnCount += (long) j.get(mu.getId());
            }
            j.put(mu.getId(), warnCount);
            try (FileWriter jf = new FileWriter("warning.json")) {
              jf.write(j.toJSONString());
            }
            eb.addField("@" + u.getName() + " warned @" + mu.getName() + "#" + mu.getDiscriminator(), "Reason: `" + umsg.replaceFirst(args[1], "") + "` (**" + warnCount + "**/**5**)", false);
            GO.getTextChannelById(LC_ID).sendMessage(eb.build()).queue();
            m.addReaction("\uD83D\uDC4D").queue();
          }
          break;
        } case "purge": {
          if (g.getMember(u).getRoles().stream().filter(r -> r.getName().equals("Staff")).collect(Collectors.toList()).size() > 0) {
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

  private void cmdX(Guild g, Message m) {
    if (!m.getAuthor().getId().equals("227067574469394432")) {
      String mc = m.getContentStripped().replaceFirst("@Skript-Bot ", "").replaceFirst("<@227067574469394432> ", "");
      if (!m.isFromType(ChannelType.PRIVATE)) {
        if (m.getChannel().getId().equals(LC_ID) || (m.getChannel().getId().equals("237960698854899713") && !(g.getMember(m.getAuthor()).getRoles().stream().filter(r -> r.getName().equals("Staff")).collect(Collectors.toList()).size() > 0) && !tf.matcher(m.getContentStripped().toLowerCase()).find())) {
          m.delete().queue();
        } else if (m.getContentStripped().toLowerCase().startsWith("@skript-bot") && validCmd(mc.split(" "))) {
          prSysI(g, (TextChannel) m.getChannel(), m.getAuthor(), "executed: `" + mc + "`");
          cmd(mc.split(" "), g.getId(), m);
        }
      } else if (validCmd(mc.split(" "))) {
        prSysI("Private", m.getAuthor(), "executed: `" + mc + "`");
        cmd(mc.split(" "), "", m);
      }
    }
  }

  private class MessageListener extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent e) {
      cmdX(e.getGuild(), e.getMessage());
    }
    public void onMessageUpdate(MessageUpdateEvent e) {
      cmdX(e.getGuild(), e.getMessage());
    }
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
      if (e.getChannel().getId().equals(SC.getId())) {
        if (GO.getMember(e.getUser()).getRoles().stream().filter(r -> r.getName().equals("Staff")).collect(Collectors.toList()).size() > 0) {
          if (e.getReactionEmote().getName().equals("\uD83D\uDC4D")) {
            if (SC.getMessageById(e.getMessageId()).complete().getReactions().get(0).getCount() < 3) {
              List<MessageEmbed.Field> mel = SC.getMessageById(e.getMessageId()).complete().getEmbeds().get(0).getFields();
              Member am = GO.getMemberById(mel.get(0).getValue());
              String a = mel.get(1).getValue();
              if (GO.getMember(e.getUser()).getRoles().stream().filter(r -> r.getName().equals("Staff")).collect(Collectors.toList()).size() > 0) {
                for (TextChannel tci : GO.getTextChannels()) {
                  if (tci.getName().equals(a)) {
                    SC.sendMessage("**Addon Channel:** `#" + a + "` already exists!").queue();
                    return;
                  }
                }
                if (!(GO.getMember(am.getUser()).getRoles().stream().filter(r -> r.getName().equals("Addon Dev")).collect(Collectors.toList()).size() > 0)) {
                  GO.getController().addRolesToMember(am, GO.getRoleById("138470986809999360")).queue();
                }
                GO.getController().getGuild().getCategoryById("360741974548152320").createTextChannel(a).queue(grac -> {
                  grac.createPermissionOverride(am).setAllow(MANAGE_WEBHOOKS, MANAGE_CHANNEL, MESSAGE_MANAGE).queue();
                  grac.createInvite().setTemporary(false).setMaxAge(0).queue();
                  grac.getManager().setTopic("v0.0.0 | Forums: " + mel.get(2).getValue() + " | Invite: " + grac.getInvites().complete().get(0) + " |\n\nOnly use this channel for " + a + " related chat.").queue();
                  ((TextChannel) grac).sendMessage(am.getAsMention() + " TEMPORARY MESSAGE please remove this after you have read and understand it.\n\n" +
                      "This is your channel for related chat about your addon, you can manage this channel, change the topic, create WebHooks, and remove messages, if this is abused this permission can be removed!\n\n" +
                      "You also now have access to #addon-updates where you can post updates for your addon, please make sure to stick to the format, and only use this channel for posting addon or tool updates.\n\n" +
                      "WebHooks allow you to sync this channel and your addon's GitHub repo together so when you get an issue, or commit to GitHub it will be posted in this channel, Help link: https://support.discordapp.com/hc/en-us/articles/228383668-Intro-to-Webhooks\n\n" +
                      "If you have any other questions ask a member of Staff!").queue();
                  AC.sendMessage("Addon Channel: " + jda.getTextChannelById(grac.getId()).getAsMention() + " has just been created!").queue();
                });
              }
            }
          }
        }
      }
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
      SC.sendMessage(eb.build()).queue();
    }
  }

  private boolean validCmd(String[] args) {
    return cmds.contains(args[0].toLowerCase()) && (cargs.get(cmds.indexOf(args[0])).equals("") || args.length > 1);
  }

  private MessageEmbed embedBuilder(String text) {
    EmbedBuilder eb = new EmbedBuilder();
    try {
      JSONObject j = (JSONObject) new JSONParser().parse(text);
      if (j.containsKey("color")) eb.setColor(Color.decode((String) j.get("color")));
      if (j.containsKey("desc")) eb.setDescription(((String) j.get("desc")).replaceAll("%nl%", System.lineSeparator()));
      if (j.containsKey("author")) {
        JSONObject jo = (JSONObject) j.get("author");
        eb.setAuthor((String) jo.get("content"), (String) jo.get("linkurl"), (String) jo.get("iconurl"));
      }
      if (j.containsKey("title")) {
        JSONObject jo = (JSONObject) j.get("title");
        if (jo.containsKey("titleurl")) {
          eb.setTitle((String) jo.get("title"), (String) jo.get("titleurl"));
        } else {
          eb.setTitle((String) jo.get("title"));
        }
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
        if (!i.getCode().equals("0lx4QhQvwelCZbEX")) {
          desc.append(", [#").append(i.getChannel().getName()).append("](https://discord.gg/").append(i.getCode()).append(") (").append(i.getUses()).append(")");
        }
      }
    } catch (Exception x) {
      x.printStackTrace();
    }
    return desc.toString().replaceFirst(",", "");
  }
}
