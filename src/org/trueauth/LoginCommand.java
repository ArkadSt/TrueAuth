/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author arkad
 */
public class LoginCommand implements CommandExecutor {

    private final Main main;

    public LoginCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            //Check if not logged in
            if ((new File(main.getDataFolder(), "playerdata/" + player.getUniqueId().toString() + ".dat")).exists()) {

                if (args.length == 1) {

                    try {
                        File authdata_folder = new File(this.main.getDataFolder(), "authdata");
                        File authdata = new File(authdata_folder, player.getUniqueId().toString() + ".txt");
                        if (authdata.exists()) {
                            Scanner authdata_reader = new Scanner(authdata);
                            String password = authdata_reader.next();
                            authdata_reader.close();
                            if (args[0].equals(password)) {

                                player.kickPlayer(ChatColor.DARK_GREEN + "You have been authenticated. Now re-join the server and play.");
                                main.restorePlayerData(player.getUniqueId());
                                Main.disconnected_player_array.add(new DisconnectedPlayer(player));
                                
                            } else {
                                player.sendMessage(ChatColor.RED + "Wrong password.");
                            }

                        } else {
                            player.sendMessage(ChatColor.RED + "You haven't registered yet.");
                        }
                    } catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "Something went wrong. Please contact server administrator.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "/login password");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You have already logged in.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player in order to run this command");
        }
        return true;
    }

}
