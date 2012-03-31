package com.douglas_srs.AfkManager;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

public class AfkManagerConfig {
	public static AfkManager plugin;
	public static File configFile;
    public static FileConfiguration config;
    public static String defaultKey = "afkmanager.actions.";
    public static String defaultKey2 = "afkmanager.nomoneyaction.";
    
    public void saveYamls() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public AfkManagerConfig(AfkManager instance)
    {
        plugin = instance;
    }
	
	public static Boolean getConfigStatus(String actionName)
	{
		return config.getBoolean(defaultKey + actionName + ".enabled");
	}
	
	public static String getConfigAction(String actionName)
	{
		return config.getString(defaultKey + actionName + ".action");
	}
	
	public static Integer getConfigTime(String actionName)
	{
		return config.getInt(defaultKey + actionName + ".time");
	}
	
	public static Boolean getConfigBC(String actionName)
	{
		return config.getBoolean(defaultKey + actionName + ".broadcast");
	}
	
	public static String getConfigBCMessage(String actionName)
	{
		return config.getString(defaultKey + actionName + ".bcmessage");
	}
	
	public static Boolean getConfigAlertPlayer(String actionName)
	{
		return config.getBoolean(defaultKey + actionName + ".alertplayer");
	}
	
	public static String getConfigPlayerMessage(String actionName)
	{
		return config.getString(defaultKey + actionName + ".playermessage");
	}
	
	public static Boolean getConfigLog(String actionName)
	{
		return config.getBoolean(defaultKey + actionName + ".log");
	}
	
	public static String getConfigCommand(String actionName)
	{
		return config.getString(defaultKey + actionName + ".command");
	}
	
	public static Integer getConfigMoney()
	{
		return config.getInt("afkmanager.money");
	}
	
	//MONEY STUFF
	
	public static Boolean getConfigNoMoneyStatus()
	{
		return config.getBoolean(defaultKey2 + "enabled");
	}
	
	public static String getConfigNoMoneyAction()
	{
		return config.getString(defaultKey2 + "action");
	}
	
	public static Integer getConfigNoMoneyTime()
	{
		return config.getInt(defaultKey2 + "time");
	}
	
	public static Boolean getConfigNoMoneyBC()
	{
		return config.getBoolean(defaultKey2 + "broadcast");
	}
	
	public static String getConfigNoMoneyBCMessage()
	{
		return config.getString(defaultKey2 + "bcmessage");
	}
	
	public static Boolean getConfigNoMoneyAlertPlayer()
	{
		return config.getBoolean(defaultKey2 + "alertplayer");
	}
	
	public static String getConfigNoMoneyPlayerMessage()
	{
		return config.getString(defaultKey2 + "playermessage");
	}
	
	public static Boolean getConfigNoMoneyLog()
	{
		return config.getBoolean(defaultKey2 + "log");
	}
	
	public static String getConfigNoMoneyCommand()
	{
		return config.getString(defaultKey2 + "command");
	}
	
	public static Integer getConfigMoveRadius()
	{
		return config.getInt("afkmanager.moveradius");
	}
	
}
