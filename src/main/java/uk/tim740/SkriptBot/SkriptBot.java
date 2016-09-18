package uk.tim740.SkriptBot;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.GuildManager;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Created by tim740 on 18/09/2016
 */

public class SkriptBot {
    private static JDA jda;

    public static void main(String[] args){
        if (args.length < 1){
            System.out.println("[Skript-Bot]: No Token Specified");
            System.exit(0);
        }
        try {
            jda = new JDABuilder().setBotToken(args[0]).addListener(new MessageListener()).buildBlocking();
            System.out.println("[Skript-Bot]: Successfully Connected to Skript-Chat!");
        } catch (LoginException e) {
            System.out.println("[Skript-Bot]: Invalid Token: " + args[0]);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("[Skript-Bot]: " + e.getMessage());
            System.exit(0);
        }

    }
    private static class MessageListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent e){
            if (!e.getMessage().getAuthor().getUsername().equals("Skript-Bot")) {
                if (e.getMessage().getContent().startsWith("@Skript-Bot")) {
                    String[] msg = e.getMessage().getContent().split(" ");
                    User user = e.getMessage().getAuthor();
                    ArrayList<String> cl = e.getGuild().getRolesForUser(user).stream().map(Role::getName).collect(Collectors.toCollection(ArrayList::new));
                    if (Objects.equals(msg[1], "help")) {
                        user.getPrivateChannel().sendMessage(
                                "Commands: (All Commands start with `@Skript-Bot`) \n\n" +
                                        "**info** (Returns Info about me) \n" +
                                        "**joinlink** (Returns the Join link for Skript-Chat) \n");
                        if (cl.contains("Staff")) {
                            user.getPrivateChannel().sendMessage(
                                    "Admin Commands: \n\n" +
                                            "**kick <user>** (kicks a user)\n");
                        }
                        if (e.getMessage().getAuthor().getUsername().equals("tim740")) {
                            user.getPrivateChannel().sendMessage("**stop** (Stops Skript-Bot)");
                        }
                        e.getMessage().getChannel().sendMessage("I've sent a list of commands to you @" + e.getMessage().getAuthor().getUsername());
                    } else if (Objects.equals(msg[1], "info")) {
                        e.getMessage().getChannel().sendMessage(
                                "Creator: @tim740#1139 \n" +
                                        "Source: https://github.com/tim740/Skript-Bot\n" +
                                        "Creation Date: 18/09/2016");
                    } else if (Objects.equals(msg[1], "joinlink")) {
                        e.getMessage().getChannel().sendMessage("Skript-Chat Join Link: https://discord.gg/0lx4QhQvwelCZbEX");
                    } else if (Objects.equals(msg[1], "kick")) {
                        if (cl.contains("Staff")) {
                            if (msg[2].contains("@")) {
                                new GuildManager(e.getGuild()).kick(user);
                                e.getMessage().getChannel().sendMessage("Kicked: " + user);
                            } else {
                                e.getMessage().getChannel().sendMessage("You need to provide a user name!");
                            }
                        } else {
                            e.getMessage().getChannel().sendMessage(":x: You do not have the right permissions :x:");
                        }
                    } else if (Objects.equals(msg[1], "stop") || (Objects.equals(msg[1], "fuck") && (Objects.equals(msg[2], "off")))) {
                        if (e.getMessage().getAuthor().getUsername().equals("tim740")) {
                            e.getMessage().getChannel().sendMessage("Closing...");
                            System.exit(0);
                        } else {
                            e.getMessage().getChannel().sendMessage(":x: Only tim740 can stop me! :x:");
                        }
                    } else {
                        e.getMessage().getChannel().sendMessage("Did you mean `@Skript-Bot help` " + e.getMessage().getAuthor().getUsername() +"?");
                    }
                }
            }
        }
        @Override
        public void onGuildMemberJoin(GuildMemberJoinEvent e) {
            e.getUser().getPrivateChannel().sendMessage(
                    "Welcome to Skript-Chat's Discord! \n" +
                    "You can get my commands by doing `@Skript-Bot help` \n\n" +
                    "Rules: \n" +
                    "   1. Use the right channels.\n" +
                    "   2. Don't scam, spam or disrespect people. \n\n" +
                    "If you an addon dev and would like the @Addon Developer Rank, Proved the link to your post so we can prove that it's really you, then you will be able to post updates in #addon-updates (Please try to stick to the default format, your allowed to leave comments too if you wish) \n\n" +
                    "If you would like to add a bot but need authorization you can message one of the staff members or @Staff \n\n" +
                    "@Staff If you need a Staff Member to make you Supporter, they will need proof still. \n\n" +
                    "If you need any more help ask a staff member :)");
        }
    }
}
