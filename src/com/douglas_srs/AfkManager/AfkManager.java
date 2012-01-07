package com.douglas_srs.AfkManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import net.milkbowl.vault.economy.Economy;

/**
 * AfkManager for Bukkit
 *
 * @author douglas_srs
 */

public class AfkManager extends JavaPlugin
{
	public static AfkManager instance;
	public static Economy economy = null;
    public static final Logger log = Logger.getLogger("Minecraft");
    private final AfkManagerPlayerListener playerListener = new AfkManagerPlayerListener(this);
    public Boolean usingpermissions = false;
    public static HashMap<String, Long> lastonline = new HashMap<String, Long>();
    public Integer afkmanagerseconds = 180;
    public String afkmanagerkickmessage = "You've been kicked for being AFK.";
    public String afkmanagerkillmessage = "You've been killed for being AFK.";
    public String afkmanagermoneymessage = "You lost %MONEY% for being AFK.";
    public String afkmanagerkicklog = "%NAME% got kicked for being AFK.";
    public String afkmanagerkilllog = "%NAME% got killed for being AFK.";
    public String afkmanagermoneylog = "%NAME% lost 0 money for being AFK.";
    public String afkmanagernomoneylog = "%NAME% got killed for being AFK and does not having enough money to pay!.";
    public String afkmanagernomoney = "You've been killed for being AFK and does not having enough money to pay!";
    public Boolean afkmanagerbroadcast = false;
    public Boolean afkmanagerkillifnomoney = true;
    public List<String> afkmanagerexempts = Arrays.asList("");
    public Boolean afkmanagerkick = true;
    public Boolean afkmanagerkill = false;
    public Boolean normalkill = true;
    public int afkmanagermoney = 0;
    public String plugintag = ChatColor.AQUA + "[AfkManager]" + ChatColor.WHITE;
    public String plugintagnocolor = "[AfkManager]";

    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    public void onEnable()
    {
    	setupEconomy();
    	instance = this;
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_BED_LEAVE, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.ENTITY_DEATH, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Event.Priority.Low, this);

        reload();

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                for (Player player : getServer().getOnlinePlayers())
                {
                    if (!afkmanagerexempts.contains(player.getName().toLowerCase()))
                    {
                        if (!player.hasPermission("afkmanager.exempt"))
                        {
                            if (lastonline.get(player.getName().toLowerCase()) == null)
                            {
                                lastonline.put(player.getName().toLowerCase(),System.currentTimeMillis()/1000);
                            }

                            if (lastonline.get(player.getName().toLowerCase()).longValue() < (System.currentTimeMillis()/1000)-afkmanagerseconds)
                            {
                            	
                            	if (afkmanagermoney > 0) {
                            	if (economy.has(player.getName(), afkmanagermoney)){
                            		economy.withdrawPlayer(player.getName(), afkmanagermoney);
                            		playerAction(player);
                            		String moneymessage = afkmanagermoneylog;
                            		moneymessage = moneymessage.replace("%NAME%",player.getName());
                                	moneymessage = moneymessage.replace("%MONEY%",economy.format(afkmanagermoney));
                            		player.sendMessage(plugintag + moneymessage);
                            		String tempmessage = afkmanagermoneylog;
                            		if (afkmanagermoneylog.trim() != "" && afkmanagermoneylog != null)
                                    {
                                    	tempmessage = afkmanagermoneylog.replace("%NAME%",player.getName());
                                    	tempmessage = tempmessage.replace("%MONEY%",economy.format(afkmanagermoney));
                                        log.info(plugintagnocolor + tempmessage);
                                    }
                            		if (afkmanagerbroadcast)
                                    {
                            			tempmessage = afkmanagermoneylog.replace("%NAME%",player.getName());
                                    	tempmessage = tempmessage.replace("%MONEY%",economy.format(afkmanagermoney));
                                    	getServer().broadcastMessage(plugintag + tempmessage);
                                    }
                            	} else {
                            		if (afkmanagerkillifnomoney) {
                            		normalkill = false;
                            		player.damage(10000);
                            		playerAction(player);
                            		if (afkmanagernomoneylog.trim() != "" && afkmanagernomoneylog != null)
                                    {
                            			log.info(plugintagnocolor + afkmanagernomoneylog.replace("%NAME%",player.getName()));
                                    }
                            		}
                            		if (afkmanagerbroadcast)
                                    {
                                    	getServer().broadcastMessage(plugintag + afkmanagernomoneylog.replace("%NAME%",player.getName()));
                                    }
                            		
                            	}
                            	} else                            	
                            	if (afkmanagerkill) {
                            		normalkill = true;
                            		player.damage(10000);
                            		playerAction(player);
                            		if (afkmanagerkilllog.trim() != "" && afkmanagerkilllog != null)
                                    {
                                        log.info(plugintagnocolor + afkmanagerkilllog.replace("%NAME%",player.getName()));
                                    }
                            		
                                    if (afkmanagerbroadcast)
                                    {
                                    	getServer().broadcastMessage(plugintag + afkmanagerkilllog.replace("%NAME%",player.getName()));
                                    }
                            		
                            	} else                            	
                            	if (afkmanagerkick) { 
                                player.kickPlayer("AfkManager!");
                                if (afkmanagerkicklog.trim() != "" && afkmanagerkicklog != null)
                                {
                                    log.info(plugintagnocolor + afkmanagerkicklog.replace("%NAME%",player.getName()));
                                }
                            	}

                            }
                        }
                    }
                }
            }
        }, 5, 20);
    }

    public void onDisable()
    {
        log.info(getDescription().getName()+" version "+getDescription().getVersion()+" is disabled!");
        getServer().getScheduler().cancelTasks(this);
    }

    @SuppressWarnings("deprecation")
    public void reload()
    {
        getConfiguration().load();
        afkmanagerseconds = getConfiguration().getInt("afkmanager.seconds",180);
        afkmanagerkickmessage = getConfiguration().getString("afkmanager.kickmessage","You've been kicked for being AFK.");
        afkmanagerbroadcast = getConfiguration().getBoolean("afkmanager.broadcast",false);
        afkmanagerkicklog = getConfiguration().getString("afkmanager.kicklog","%NAME% got kicked for being AFK.");
        afkmanagerkilllog = getConfiguration().getString("afkmanager.killlog","%NAME% got killed for being AFK.");
        afkmanagermoneylog = getConfiguration().getString("afkmanager.moneylog","%NAME% lost %MONEY% for being AFK.");
        afkmanagernomoneylog = getConfiguration().getString("afkmanager.nomoneylog","%NAME% got killed for being AFK and does not having enough money to pay!.");
        afkmanagernomoney = getConfiguration().getString("afkmanager.nomoney","You've been killed for being AFK and does not having enough money to pay!");
        afkmanagerexempts = Arrays.asList(getConfiguration().getString("afkmanager.exempts","").toLowerCase().split(","));
        afkmanagerkick = getConfiguration().getBoolean("afkmanager.kick",true);
        afkmanagerkillifnomoney = getConfiguration().getBoolean("afkmanager.killifnomoney",true);
        afkmanagerkill = getConfiguration().getBoolean("afkmanager.kill",false);
        afkmanagermoney = getConfiguration().getInt("afkmanager.money",0);
        afkmanagerkillmessage = getConfiguration().getString("afkmanager.killmessage","You've been killed for being AFK.");
        afkmanagermoneymessage = getConfiguration().getString("afkmanager.moneymessage","You lost %MONEY% for being AFK.");
        getConfiguration().save();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("afkmanager"))
        {
            if(args.length >= 1)
            {
        if(args[0].equalsIgnoreCase("reload"))
        {
            if((sender instanceof Player && ((Player)sender).hasPermission("afkmanager.admin")) || !(sender instanceof Player))
            {
                reload();
                if(sender instanceof Player){
                    ((Player)sender).sendMessage(plugintag + "Config reloaded");
                }
                else
                    log.info(plugintagnocolor + "Config reloaded");
            }
        }
            }
        }
        return true;
    }

    public void playerAction(Player player)
    {
           lastonline.put(player.getName().toLowerCase(),Long.valueOf(System.currentTimeMillis()/1000));
    }
}