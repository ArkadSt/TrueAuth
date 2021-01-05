/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author arkad
 */
public class ChangePasswordCommand implements CommandExecutor {

    private final Main main;

    public ChangePasswordCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            /*if ((new File(main.getDataFolder(), "playerdata/" + player.getUniqueId().toString() + ".dat")).exists()) {
                player.sendMessage(ChatColor.RED + "You must be logged in in order to use this command.");
                return true;

            }*/
            // Check if logged in
            if (!(new File(main.getDataFolder(), "playerdata/" + player.getUniqueId().toString() + ".dat")).exists()) {
                if (args.length == 2) {
                    if (args[0].equals(args[1])) {
                        try {
                            File authdata = new File(this.main.getDataFolder(), "authdata/" + player.getUniqueId().toString() + ".txt");
                            if (authdata.exists()) {
                                FileWriter authdata_writer = new FileWriter(authdata);
                                authdata_writer.write(args[0]);
                                authdata_writer.close();
                                player.sendMessage(ChatColor.GREEN + "You have changed your password successfully.");

                            } else {
                                player.sendMessage(ChatColor.RED + "Something went wrong. Please contact server admin.");
                            }
                        } catch (IOException e) {
                            player.sendMessage(ChatColor.RED + "Something went wrong. Please contact server admin.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Passwords do not match. Try again");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "/changepassword password password");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You must be logged in in order to use this command.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player in order to run this command");
        }
        return true;
    }

}
