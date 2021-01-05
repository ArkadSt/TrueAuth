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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author arkad
 */
public class Main extends JavaPlugin {

    public static ArrayList<DisconnectedPlayer> disconnected_player_array = new ArrayList();
    public static HashMap<UUID,PermissionAttachment> perms = new HashMap();
    public FileConfiguration config = this.getConfig();
    
    public Location getLobbyLocation() {

        if (config.getBoolean("custom_location")) {
            String world = config.getString("CustomLocationTrue.world");
            double X = config.getDouble("CustomLocationTrue.X");
            double Y = config.getDouble("CustomLocationTrue.Y");
            double Z = config.getDouble("CustomLocationTrue.Z");
            double Yaw = config.getDouble("CustomLocationTrue.Yaw");
            double Pitch = config.getDouble("CustomLocationTrue.Pitch");

            return new Location(getServer().getWorld(world), X, Y, Z, (float) Yaw, (float) Pitch);
        } else {
            return getServer().getWorld("world").getSpawnLocation();
        }
    }

    public void restorePlayerData(UUID uuid) throws IOException {

        File playerdata_in = new File(getDataFolder(), "playerdata/" + uuid.toString() + ".dat");
        File playerdata_out = new File(getServer().getWorldContainer(), "world/playerdata/" + uuid.toString() + ".dat");
        
        File advancements_in = new File(getDataFolder(), "advancements/" + uuid.toString() + ".json");
        File advancements_out = new File(getServer().getWorldContainer(), "world/advancements/" + uuid.toString() + ".json");
        
        File stats_in = new File(getDataFolder(), "stats/" + uuid.toString() + ".json");
        File stats_out = new File(getServer().getWorldContainer(), "world/stats/" + uuid.toString() + ".json");
        
        getServer().savePlayers();
        
        Files.copy(playerdata_in.toPath(), playerdata_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(advancements_in.toPath(), advancements_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(stats_in.toPath(), stats_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        playerdata_in.delete();
        advancements_in.delete();
        stats_in.delete();
        
    }

    @Override
    public void onEnable() {

        if (!(new File(this.getDataFolder(), "config.yml")).exists()) {
            this.saveDefaultConfig();
        }
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getCommand("register").setExecutor(new RegisterCommand(this));
        this.getCommand("login").setExecutor(new LoginCommand(this));
        this.getCommand("changepassword").setExecutor(new ChangePasswordCommand(this));
        this.getCommand("trueauth").setExecutor(new TrueAuthCommand(this));
    }

    @Override
    public void onDisable() {
        perms.forEach((key, value) -> value.remove());
    }

}
