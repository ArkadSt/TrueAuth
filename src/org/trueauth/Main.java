/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author arkad
 */
public class Main extends JavaPlugin {

    public static ArrayList<DisconnectedPlayer> disconnected_player_array = new ArrayList();
    public static HashMap<UUID, Integer> not_logged_in_player_collection = new HashMap();
    public FileConfiguration config;
    
    @Override
    public void onEnable() {

        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }
        
        config = getConfig();
        
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        getCommand("trueauth").setExecutor(new TrueAuthCommand(this));
    }

    @Override
    public void onDisable() {

    }

}
