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
public class RegisterCommand implements CommandExecutor {

    private final Main main;

    public RegisterCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            try {
                File authdata_folder = new File(this.main.getDataFolder(), "authdata");
                if (!authdata_folder.exists()) {
                    authdata_folder.mkdir();
                }
                File authdata = new File(authdata_folder, player.getUniqueId().toString() + ".txt");
                if (!authdata.exists()) {
                    if (args.length == 2) {
                        if (args[0].equals(args[1])) {
                            authdata.createNewFile();
                            FileWriter authdata_writer = new FileWriter(authdata);
                            authdata_writer.write(args[0]);
                            authdata_writer.close();
                            player.sendMessage(ChatColor.GREEN + "You have registered successfully. Now Login.");

                        } else {
                            player.sendMessage(ChatColor.RED + "Passwords do not match. Try again");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "/register password password");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You have already registered.");
                }
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Something went wrong. Please contact server admin.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player in order to run this command");
        }
        return true;
    }

}
