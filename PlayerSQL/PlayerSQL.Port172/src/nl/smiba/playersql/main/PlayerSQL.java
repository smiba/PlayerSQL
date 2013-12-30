package nl.smiba.playersql.main;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;


public class PlayerSQL extends JavaPlugin implements Listener {
	
        Boolean debug = false;
        String mysqlhost;
        String mysqluser;
        String mysqlpassword;
		
		private void printDebug(String arg){
			if (debug){
			System.out.print(arg);
			}
		}
		
		private void printDebug(String arg,String arg2){
			if (debug){
				System.out.print(arg);
			}else{
				System.out.print(arg2);
			}
		}
		
        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerJoin(PlayerJoinEvent event){
	        	printDebug("INSERT INTO players (playername) VALUES ('" + event.getPlayer().getName() + "');");
	            mysqlquery("INSERT INTO players (playername) VALUES ('" + event.getPlayer().getName() + "');");
	        }
        
        @EventHandler(priority = EventPriority.LOW)
        public void onPlayerQuit(PlayerQuitEvent event){
        		printDebug("DELETE FROM players WHERE playername='" + event.getPlayer().getName() + "';");
                mysqlquery("DELETE FROM players WHERE playername='" + event.getPlayer().getName() + "';");
        }
        	
        @Override
	    public void onEnable(){
        	System.out.print("PlayerSQL: Loading..");
	        loadConfiguration();
	        System.out.print("PlayerSQL: Configuration loaded!");
	        printDebug("PlayerSQL: Debug Enabled");
	        printDebug("PlayerSQL: Registrating Events");
	        getServer().getPluginManager().registerEvents(this, this);
	        printDebug("PlayerSQL: Clearing the SQL table");
		    if(!mysqlqueryR("TRUNCATE TABLE players;")){
		    	System.out.print("PlayerSQL: Something went wrong while clearing the tables.. Maybe they don't exist? Creating them now");
		        if(!mysqlqueryR("CREATE TABLE players(playername TEXT(128));")){
		        	printDebug("PlayerSQL Error! Does the user " + mysqluser + " have all read/write permissions?");
		        }
		    }              
	    }
        
		    @Override
		    public void onDisable() {
		    	printDebug("PlayerSQL is shutting down");
		    }
		    
		    public void loadConfiguration(){
		    	File configFile = new File(this.getDataFolder(), "config.yml");
		        printDebug("PlayerSQL: Checking if config.yml exists");
		        if (!configFile.exists()){
		            System.out.print("PlayerSQL: Config.yml does not exist, creating one now");
		            this.saveDefaultConfig();
		        }else{
		        	printDebug("PlayerSQL: Config.yml exists");
		        }
		        debug = getConfig().getBoolean("debug");
		        mysqlhost = getConfig().getString("connection.connection");
		        mysqluser = getConfig().getString("connection.username");
		        mysqlpassword = getConfig().getString("connection.password");
		    }
    
		    public void mysqlquery(String query){ 
		    	try{
		    		printDebug("PlayerSQL: Connecting with (connection,username,password) " + mysqlhost + ", " + mysqluser + ", " + mysqlpassword);   
		            Connection conn = DriverManager.getConnection(mysqlhost, mysqluser, mysqlpassword);
		            PreparedStatement sampleQueryStatement = conn.prepareStatement(query); 
		            sampleQueryStatement.executeUpdate();
		            sampleQueryStatement.close();
		            conn.close();
		    	}catch (SQLException e) {
		    		printDebug(e.getStackTrace().toString(), "PlayerSQL Error : " + e.getErrorCode());
				}
		    }
    
		    public boolean mysqlqueryR(String query){ 
		    	try{
		    		printDebug("PlayerSQL: Connecting with (connection,username,password) " + mysqlhost + ", " + mysqluser + ", " + mysqlpassword);   
		            Connection conn = DriverManager.getConnection(mysqlhost, mysqluser, mysqlpassword);
		            PreparedStatement sampleQueryStatement = conn.prepareStatement(query); 
		            sampleQueryStatement.executeUpdate();
		            sampleQueryStatement.close();
		            conn.close();
		            return true;
		    	}catch (SQLException e) {
		    		printDebug(e.getStackTrace().toString(), "PlayerSQL Error : " + e.getErrorCode());
		    		return false;
				}
		    }
}