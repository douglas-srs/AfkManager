package com.douglas_srs.AfkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
	public static Logger log = Logger.getLogger("Minecraft");
    private final AfkManagerPlayerListener playerListener = new AfkManagerPlayerListener(this);
    public static HashMap<String, Long> lastonline = new HashMap<String, Long>();
    public static HashMap<String, Location> lastposition = new HashMap<String, Location>();
    public List<String> afkmanagerexempts = Arrays.asList("");
    public String plugintag = ChatColor.AQUA + "[AFKManager]" + ChatColor.WHITE;
    public String defaultKey = "afkmanager.actions.";
    public Integer money = 0;
    public Integer moveradius = 5;
    
    public Boolean enabled = false;
    public String action = null;
    public Integer time = 0;
    public Boolean broadcast = false;
    public String bcmessage = null;
    public Boolean alertplayer = false;
    public String playermessage = null;
    public Boolean clog = false;
    public String command = null;
    
    //MONEY STUFF
    public Boolean nomoneyenabled = false;
    public String nomoneyaction = null;
    public Integer nomoneytime = 0;
    public Boolean nomoneybroadcast = false;
    public String nomoneybcmessage = null;
    public Boolean nomoneyalertplayer = false;
    public String nomoneyplayermessage = null;
    public Boolean nomoneyclog = false;
    public String nomoneycommand = null;
    
    //Function to get the economy plugin using Vault
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    
    //Method used to create a new config if there is no one
    private void firstRun() throws Exception {
        if(!AfkManagerConfig.configFile.exists()){
        	AfkManagerConfig.configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), AfkManagerConfig.configFile);
        }
    }
    
    //Method used to copy files
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Method to send a message to all players except the current one
    public void broadcastServer(Player pAtual, String Message)
    {
    	for(Player p : getServer().getOnlinePlayers()){
    		if (p.getName() != pAtual.getName())
    		p.sendMessage(Message);
      	  }
    }
    
    public void onEnable()
    {
    	setupEconomy();
    	instance = this;
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvents(playerListener, this);
        AfkManagerConfig.configFile = new File(getDataFolder(), "config.yml");
        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AfkManagerConfig.config = new YamlConfiguration();
        AfkManagerConfig.loadYamls();       

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                for (Player player : getServer().getOnlinePlayers())
                {
                    if (!afkmanagerexempts.contains(player.getName()))
                    {
                        if (!player.hasPermission("afkmanager.exempt"))
                        {
                            if (lastonline.get(player.getName()) == null)
                            {
                                lastonline.put(player.getName(),System.currentTimeMillis()/1000);
                            }
                            
                            FileConfiguration config = AfkManagerConfig.config;
                            ConfigurationSection act = config.getConfigurationSection("afkmanager.actions");                  
                            if (act != null) {
                                for (String key : act.getKeys(false)) {
                                	                                  
                                    enabled = AfkManagerConfig.getConfigStatus(key);
                                    action = AfkManagerConfig.getConfigAction(key).toLowerCase();
                                    time = AfkManagerConfig.getConfigTime(key);
                                    broadcast = AfkManagerConfig.getConfigBC(key);
                                    bcmessage = plugintag + AfkManagerConfig.getConfigBCMessage(key);
                                    bcmessage = bcmessage.replaceAll("&([0-9a-fA-Fk-kK-K])", "\u00a7$1");
                                    if (bcmessage != null)
                                    	bcmessage = bcmessage.replace("%NAME%",player.getName());
                                    alertplayer = AfkManagerConfig.getConfigAlertPlayer(key);
                                    playermessage = plugintag + AfkManagerConfig.getConfigPlayerMessage(key);
                                    playermessage = playermessage.replaceAll("&([0-9a-fA-Fk-kK-K])", "\u00a7$1");
                                    clog = AfkManagerConfig.getConfigLog(key);
                                    command = AfkManagerConfig.getConfigCommand(key);
                                    if (command != null)
                                    	command = command.replace("%NAME%",player.getName());
                                    
                                    //MONEY STUFF
                                    nomoneyenabled = AfkManagerConfig.getConfigNoMoneyStatus();
                                    nomoneyaction = AfkManagerConfig.getConfigNoMoneyAction();
                                    nomoneytime = AfkManagerConfig.getConfigNoMoneyTime();
                                    nomoneybroadcast = AfkManagerConfig.getConfigNoMoneyBC();
                                    nomoneybcmessage = plugintag + AfkManagerConfig.getConfigNoMoneyBCMessage();
                                    nomoneybcmessage = nomoneybcmessage.replaceAll("&([0-9a-fA-Fk-kK-K])", "\u00a7$1");
                                    if (nomoneybcmessage != null)
                                    	nomoneybcmessage = nomoneybcmessage.replace("%NAME%",player.getName());
                                    nomoneyalertplayer = AfkManagerConfig.getConfigNoMoneyAlertPlayer();
                                    nomoneyplayermessage = plugintag + AfkManagerConfig.getConfigNoMoneyPlayerMessage();
                                    nomoneyplayermessage = nomoneyplayermessage.replaceAll("&([0-9a-fA-Fk-kK-K])", "\u00a7$1");
                                    nomoneyclog = AfkManagerConfig.getConfigNoMoneyLog();
                                    nomoneycommand = AfkManagerConfig.getConfigNoMoneyCommand();
                                    money = AfkManagerConfig.getConfigMoney();
                                    moveradius = AfkManagerConfig.getConfigMoveRadius();

                                    if (nomoneycommand != null)
                                    	nomoneycommand = nomoneycommand.replace("%NAME%",player.getName());
                                        
                                	Location loc = lastposition.get(player.getName());
                                	Location newloc = player.getLocation();
                                	if (loc == null) {
                                		lastposition.put(player.getName(), newloc);
                                	} else {
                                	    //distance exceeded?
                                	    if (loc.getWorld() != newloc.getWorld() || loc.distance(newloc) > moveradius) {
                                	        //changed; re-put
                                	    	playerAction(player);
                                	    	lastposition.put(player.getName(), newloc);
                                	    }
                                	}
                                    
                                        if (lastonline.get(player.getName()).longValue() == (System.currentTimeMillis()/1000)-time)
                                        {
                                        	if (enabled){
                                        		
                                        	//MESSAGE
                                            if (action.equals("message")){
                                                if (alertplayer)
                                                	player.sendMessage(playermessage);
                                            		
                                            		if (broadcast)
                                            			broadcastServer(player, bcmessage);

                                            		if (clog)
                                                    		log.log(Level.INFO, bcmessage);
                                            }	
                                        		
                                        	//KICK
                                        	if (action.equals("kick")){
                                                player.kickPlayer(playermessage);
                                                if (broadcast)
                                                	broadcastServer(player, bcmessage);
                                                
                                                if (clog)
                                                		log.log(Level.INFO, bcmessage);
                                        	}
                                        	
                                        	//KILL
                                        	if (action.equals("kill")){
                                        		player.damage(10000);
                                        		
                                        		if (alertplayer)
                                            	player.sendMessage(playermessage);
                                        		
                                        		if (broadcast)
                                        			broadcastServer(player, bcmessage);

                                        		if (clog)
                                                		log.log(Level.INFO, bcmessage);
                                        		}
                                        		                                       	
                                        	//MONEY
                                        	if (action.equals("money")){
                                        		if (economy.has(player.getName(), money)){
                                            		economy.withdrawPlayer(player.getName(), money);
                                            		String moneymessage = playermessage;
                                            		moneymessage = moneymessage.replace("%NAME%",player.getName());
                                                	moneymessage = moneymessage.replace("%MONEY%",economy.format(money));
                                                	if (alertplayer){
                                            		player.sendMessage(moneymessage);
                                                	}
                                            		String tempmessage = "";
                                            		
                                            		if (broadcast)
                                                    {
                                            			tempmessage = bcmessage.replace("%MONEY%",economy.format(money));
                                            			broadcastServer(player, tempmessage);
                                                    } else

                                                    if (clog){
                                                    		tempmessage = bcmessage.replace("%MONEY%",economy.format(money));
                                                    		log.log(Level.INFO, bcmessage);
                                                    	}
                                            		
                                            	} else //PLAYER DOESN'T HAVE MONEY
                                            	{
                                            		if (nomoneyaction.equals("message")){
                                                        if (nomoneyalertplayer)
                                                        	player.sendMessage(nomoneyplayermessage);
                                                    		
                                                    		if (nomoneybroadcast)
                                                    			broadcastServer(player, nomoneybcmessage);

                                                    		if (nomoneyclog)
                                                            		log.log(Level.INFO, nomoneybcmessage);
                                            		}
                                            		
                                            		if (nomoneyaction.equals("kill")){
                                            			player.damage(10000);
                                                		
                                                		if (nomoneyalertplayer)
                                                    	player.sendMessage(nomoneyplayermessage);
                                                		
                                                		if (nomoneybroadcast)
                                                			broadcastServer(player, nomoneybcmessage);

                                                		if (nomoneyclog)
                                                        		log.log(Level.INFO, nomoneybcmessage);
                                            		}

                                            		if (nomoneyaction.equals("kick")){
                                            			player.kickPlayer(playermessage);
                                                        if (nomoneybroadcast)
                                                        	broadcastServer(player, nomoneybcmessage);
                                                        
                                                        if (nomoneyclog)
                                                        		log.log(Level.INFO, nomoneybcmessage);
                                            		}
                                            		
                                            		if (nomoneyaction.equals("command")){
                                            			getServer().dispatchCommand(Bukkit.getConsoleSender(), nomoneycommand);
                                                		if (nomoneyalertplayer)
                                                        	player.sendMessage(nomoneyplayermessage);
                                                        
                                                		if (nomoneybroadcast)
                                                			broadcastServer(player, nomoneybcmessage);

                                                		if (nomoneyclog)
                                                        		log.log(Level.INFO, nomoneybcmessage);
                                            		}
                                            		
                                                }
                                            	}
                                        	
                                        	//COMMAND
                                        	if (action.equals("command")){
                                        		getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                                        		if (alertplayer)
                                                	player.sendMessage(playermessage);
                                                
                                        		if (broadcast)
                                        			broadcastServer(player, bcmessage);

                                        		if (clog)
                                                		log.log(Level.INFO, bcmessage);
                                        	}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, 5, 20);
        
        log.log(Level.INFO, getDescription().getName()+" version "+getDescription().getVersion()+" enabled!");
    }
    
	public void onDisable()
    {
        log.info(getDescription().getName()+" version "+getDescription().getVersion()+" is disabled!");
        getServer().getScheduler().cancelTasks(this);
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
            	AfkManagerConfig.loadYamls();
                if(sender instanceof Player){
                    ((Player)sender).sendMessage(plugintag + "Config reloaded");
                }
                else
                    log.info(plugintag + "Config reloaded");
            }
        }
            }
        }
        return true;
    }

    public void playerAction(Player player)
    {
           lastonline.put(player.getName(),Long.valueOf(System.currentTimeMillis()/1000));
    }
    
}