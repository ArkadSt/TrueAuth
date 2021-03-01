/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author arkad
 */
public class EventListener implements Listener {

    private final Main main;

    public EventListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {

        Player player = event.getPlayer();

        if (sessionActive(player)) {
            player.sendMessage(ChatColor.BLUE + "Logged in via session.");
            return;
        }

        event.setJoinMessage("");
        player.setPlayerListName(player.getName() + ChatColor.BLUE + " [Not Logged In]");

        File playerdata_folder = new File(main.getDataFolder(), "playerdata/");
        File advancements_folder = new File(main.getDataFolder(), "advancements/");
        File stats_folder = new File(main.getDataFolder(), "stats/");
        File isOp_folder = new File(main.getDataFolder(), "op/");

        if (!playerdata_folder.exists()) {
            playerdata_folder.mkdir();
        }
        if (!advancements_folder.exists()) {
            advancements_folder.mkdir();
        }
        if (!stats_folder.exists()) {
            stats_folder.mkdir();
        }
        if (!isOp_folder.exists()) {
            isOp_folder.mkdir();
        }

        File playerdata_in = new File(main.getServer().getWorldContainer(), "world/playerdata/" + player.getUniqueId() + ".dat");
        File playerdata_out = new File(playerdata_folder, player.getUniqueId() + ".dat");

        File advancements_in = new File(main.getServer().getWorldContainer(), "world/advancements/" + player.getUniqueId() + ".json");
        File advancements_out = new File(advancements_folder, player.getUniqueId() + ".json");

        File stats_in = new File(main.getServer().getWorldContainer(), "world/stats/" + player.getUniqueId() + ".json");
        File stats_out = new File(stats_folder, player.getUniqueId() + ".json");

        File isOp_file = new File(isOp_folder, player.getUniqueId() + ".txt");

        if (!playerdata_out.exists() && !advancements_out.exists() && !isOp_file.exists()) {
            main.getServer().savePlayers();
            Files.copy(playerdata_in.toPath(), playerdata_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(advancements_in.toPath(), advancements_out.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(stats_in.toPath(), stats_out.toPath(), StandardCopyOption.REPLACE_EXISTING);

            isOp_file.createNewFile();
            FileWriter isOp_file_writer = new FileWriter(isOp_file);
            isOp_file_writer.write(Boolean.toString(player.isOp()));
            isOp_file_writer.close();
        }

        // Neutralize
        player.setOp(false);
        player.setFireTicks(0);
        player.setFlying(false);
        player.setSwimming(false);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setExp(0.0F);
        player.setLevel(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setRemainingAir(player.getMaximumAir());
        player.getInventory().clear();
        player.undiscoverRecipes(player.getDiscoveredRecipes());

        /// Remove advancements
        Iterator<Advancement> iterator = main.getServer().advancementIterator();
        while (iterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }

        /// Remove statistics
        for (Statistic statistic : Statistic.values()) {
            switch (statistic.getType()) {
                case UNTYPED:
                    player.setStatistic(statistic, 0);
                    break;
                case BLOCK:
                case ITEM:
                    for (Material material : Material.values()) {
                        try {
                            player.setStatistic(statistic, material, 0);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                    break;
                case ENTITY:
                    for (EntityType entity_type : EntityType.values()) {
                        try {
                            player.setStatistic(statistic, entity_type, 0);
                        } catch (IllegalArgumentException e) {
                        }
                    }
                    break;

            }
        }

        // Configure special state
        player.setGameMode(GameMode.ADVENTURE);
        player.setInvulnerable(true);

        player.setCanPickupItems(false);
        player.setCollidable(false);
        player.setSilent(true);
        player.setInvisible(true);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));

        /// set perms_for_everyone to false
        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        Collection<Node> user_nodes = user.getNodes();

        List<String> perms_for_everyone = main.config.getStringList("permissions.for_everyone");
        List<String> perms_if_true = main.config.getStringList("permissions.if_true");
        
        outerloop:
        // Check permissions listed in "permissions.for_everyone"
        for (String perm : perms_for_everyone) {
            // Check user permissions
            for (Node user_node : user_nodes) {
                //Check if user has the same permision
                if (user_node.getKey().equals(perm)) {
                    // Modify existing node instead of creating the new one
                    Node user_node_modified = user_node.toBuilder().value(false).build();
                    user.data().add(user_node_modified);
                    continue outerloop;
                }
            }
            // Create new node
            Node node = Node.builder(perm).value(false).build();
            user.data().add(node);
        }
        
        // Check user permissions
        for (Node user_node : user_nodes) {
            // Check whether user permission is listed in "permissions.if_true"
            if (perms_if_true.contains(user_node.getKey())) {
                if (user_node.getValue()) {
                    Node user_node_modified = user_node.toBuilder().value(false).build();
                    user.data().add(user_node_modified);
                }
            }
        }
        
        luckPerms.getUserManager().saveUser(user);

        player.teleport(getLobbyLocation());

        // Activate timeout kick timer
        int timer_id = main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {

            @Override
            public void run() {
                player.kickPlayer(ChatColor.RED + "You were kicked due to the authentication timeout");
            }

        }, main.config.getInt("timeout") * 20);

        Main.not_logged_in_player_collection.put(player.getUniqueId(), timer_id);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Check if not authorized
        if (Main.not_logged_in_player_collection.containsKey(player.getUniqueId())) {
            // Cancel timeout kick timer
            main.getServer().getScheduler().cancelTask(Main.not_logged_in_player_collection.get(player.getUniqueId()));
            Main.not_logged_in_player_collection.remove(player.getUniqueId());
            event.setQuitMessage("");
        } else {
            Main.disconnected_player_array.add(new DisconnectedPlayer(player));
        }

    }

    @EventHandler
    public void onAttacking(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;

            // Check if not authorized
            if (Main.not_logged_in_player_collection.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onSurroundingInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if not authorized
        if (Main.not_logged_in_player_collection.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isAsynchronous()) {
            Player player = event.getPlayer();

            // Check if not authorized
            if (Main.not_logged_in_player_collection.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You have to log in in order to use chat.");
                event.setCancelled(true);
            } else {
                event.getRecipients().removeIf(x -> Main.not_logged_in_player_collection.containsKey(x.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (main.config.getBoolean("disable_moving")) {
            Player player = event.getPlayer();

            // Check if not authorized
            if (Main.not_logged_in_player_collection.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    private Location getLobbyLocation() {

        if (main.config.getBoolean("custom_location")) {
            String world = main.config.getString("CustomLocationTrue.world");
            double X = main.config.getDouble("CustomLocationTrue.X");
            double Y = main.config.getDouble("CustomLocationTrue.Y");
            double Z = main.config.getDouble("CustomLocationTrue.Z");
            double Yaw = main.config.getDouble("CustomLocationTrue.Yaw");
            double Pitch = main.config.getDouble("CustomLocationTrue.Pitch");

            return new Location(main.getServer().getWorld(world), X, Y, Z, (float) Yaw, (float) Pitch);
        } else {
            return main.getServer().getWorld("world").getSpawnLocation();
        }
    }

    private boolean sessionActive(Player player) {

        long session_time = main.config.getLong("session_time") * 1000L;
        for (int x = 0; x < Main.disconnected_player_array.size(); x++) {
            if (Main.disconnected_player_array.get(x).uuid.equals(player.getUniqueId())) {
                if (System.currentTimeMillis() - Main.disconnected_player_array.get(x).leaving_time <= session_time
                        && Main.disconnected_player_array.get(x).ip.equals(player.getAddress().getAddress())) {

                    Main.disconnected_player_array.remove(x);
                    return true;
                }
            }
        }
        return false;
    }

}
