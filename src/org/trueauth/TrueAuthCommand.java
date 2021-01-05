/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author arkad
 */
public class TrueAuthCommand implements CommandExecutor {

    Main main;

    public TrueAuthCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "reload":
                    main.reloadConfig();
                    main.config = main.getConfig();
                    sender.sendMessage(ChatColor.GREEN + "Configuration file was reloaded successfully");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "No such command.");
                    break;
            }

        } else {
            sender.sendMessage(ChatColor.RED + "No such command.");
        }
        return true;
    }
}
