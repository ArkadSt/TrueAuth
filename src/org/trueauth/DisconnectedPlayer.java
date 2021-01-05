/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.trueauth;

import java.net.InetAddress;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author arkad
 */
public class DisconnectedPlayer {

    UUID uuid;
    long leaving_time;
    InetAddress ip;

    public DisconnectedPlayer(Player player) {
        uuid = player.getUniqueId();
        leaving_time = System.currentTimeMillis();
        ip = player.getAddress().getAddress();
    }
}
