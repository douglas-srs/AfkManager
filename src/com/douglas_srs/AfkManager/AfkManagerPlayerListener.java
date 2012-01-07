package com.douglas_srs.AfkManager;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class AfkManagerPlayerListener extends PlayerListener
{
    public static AfkManager plugin;
    public static Boolean killmessage = false;
    public AfkManagerPlayerListener(AfkManager instance)
    {
        plugin = instance;
    }

    public void onPlayerJoin(PlayerJoinEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerChat(PlayerChatEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onItemHeldChange(PlayerItemHeldEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerBedLeave(PlayerBedLeaveEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
    {
        plugin.playerAction(event.getPlayer());
    }

    public void onPlayerKick(PlayerKickEvent event)
    {
        if (event.getReason().equals("AfkManager!"))
        {
            event.setReason(plugin.afkmanagerkickmessage);

            if (plugin.afkmanagerbroadcast)
            {
            	plugin.getServer().broadcastMessage(plugin.plugintag + plugin.afkmanagerkicklog.replace("%NAME%",event.getPlayer().getName()));
                //event.setLeaveMessage(plugin.plugintag + plugin.afkmanagerkickmessage.replace("%NAME%",event.getPlayer().getName()));
            }
        }
    }
       
    public void onPlayerRespawn(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.normalkill) {
        if (plugin.afkmanagerkill) {
        	plugin.playerAction(player);
        	player.sendMessage(plugin.plugintag + plugin.afkmanagerkillmessage);
        } 
        return;       
        }
        
    }
}