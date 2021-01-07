/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
                                restorePlayerData(player);
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

    private void restorePlayerData(Player player) throws IOException {

        File playerdata_in = new File(main.getDataFolder(), "playerdata/" + player.getUniqueId().toString() + ".dat");
        File playerdata_out = new File(main.getServer().getWorldContainer(), "world/playerdata/" + player.getUniqueId().toString() + ".dat");

        File advancements_in = new File(main.getDataFolder(), "advancements/" + player.getUniqueId().toString() + ".json");
        File advancements_out = new File(main.getServer().getWorldContainer(), "world/advancements/" + player.getUniqueId().toString() + ".json");

        File stats_in = new File(main.getDataFolder(), "stats/" + player.getUniqueId().toString() + ".json");
        File stats_out = new File(main.getServer().getWorldContainer(), "world/stats/" + player.getUniqueId().toString() + ".json");

        File isOp_file = new File(main.getDataFolder(), "op/" + player.getUniqueId() + ".txt");

        Files.copy(playerdata_in.toPath(), playerdata_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(advancements_in.toPath(), advancements_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(stats_in.toPath(), stats_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        Scanner isOp_reader = new Scanner(isOp_file);
        player.setOp(isOp_reader.nextBoolean());
        isOp_reader.close();
        
        playerdata_in.delete();
        advancements_in.delete();
        stats_in.delete();
        isOp_file.delete();

    }

}
