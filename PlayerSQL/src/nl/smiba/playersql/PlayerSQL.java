package nl.smiba.playersql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerSQL extends JavaPlugin implements Listener {
	File configFile;
	FileConfiguration config;
	Boolean debug = false;
	String mysqlhost;
	String mysqluser;
	String mysqlpassword;
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event){
	//Add player to SQL
	try {
		if (debug == true){
		getLogger().info("INSERT INTO players (playername) VALUES ('" + event.getPlayer().getName() + "');");
		}
		mysqlquery("INSERT INTO players (playername) VALUES ('" + event.getPlayer().getName() + "');");
	} catch (SQLException e) {
		e.printStackTrace();
	}
	/**	if (debug == true){
		System.out.print("[PlayerSQL] Player *NAAM HIER NOG* successfully removed from the database!");
	} */
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event){
        //Remove player from SQL
    	try {
    		if (debug == true){
    		getLogger().info("DELETE FROM players WHERE playername='" + event.getPlayer().getName() + "';");
    		}
    		mysqlquery("DELETE FROM players WHERE playername='" + event.getPlayer().getName() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	@Override
    public void onEnable(){
		System.out.print("PlayerSQL: Loading..");
		loadConfiguration();
		System.out.print("PlayerSQL: Configuration loaded!");
		if (debug == true){
			System.out.print("PlayerSQL: Debug Enabled");
		}
		if (debug == true){
			System.out.print("PlayerSQL: Registrating Events");
		}
		getServer().getPluginManager().registerEvents(this, this);
		File configFile = new File(this.getDataFolder(), "config.yml");
		if (debug == true){
		System.out.print("PlayerSQL: Checking if config.yml exists");
		}
		if (!configFile.exists()){
			System.out.print("PlayerSQL: Config.yml does not exist, creating one now");
			this.saveDefaultConfig();
		}else{
			if (debug == true){
			System.out.print("PlayerSQL: Config.yml exists");
			}
		}
    	try {
    		if (debug == true){
    		System.out.print("PlayerSQL: Clearing the SQL table");
    		}
			mysqlquery("TRUNCATE TABLE players;");
		} catch (SQLException e) {
			System.out.print("PlayerSQL: Something went wrong while clearing the tables.. Maybe they don't exist? Creating them now");
			try {
				mysqlquery("CREATE TABLE players(playername TEXT(128));");
			} catch (SQLException e1) {
				System.out.print("PlayerSQL: !!! CANT CREATE TABLE - DOES THE USER '" + mysqluser + "' HAVE THE RIGHT PERMISSIONS!?");
				e1.printStackTrace();
			}
		}	
	}
    @Override
    public void onDisable() {
    	if (debug == true){
    		getLogger().info("PlayerSQL is shutting down");
    	}
    }
    public void loadConfiguration(){
		debug = getConfig().getBoolean("debug");
		mysqlhost = getConfig().getString("connection.connection");
		mysqluser = getConfig().getString("connection.username");
		mysqlpassword = getConfig().getString("connection.password");
   /**     getConfig().options().copyDefaults(true);
        saveConfig(); */
    }
    public void mysqlquery(String query) throws SQLException { //Change "SampleFunction" to your own function name (Can be anything, unless it already exists)
		if (debug == true){
		System.out.print("PlayerSQL: Connecting with (connection,username,password) " + mysqlhost + ", " + mysqluser + ", " + mysqlpassword);
		}
    	Connection conn = DriverManager.getConnection(mysqlhost, mysqluser, mysqlpassword); //Creates the connection
		PreparedStatement sampleQueryStatement = conn.prepareStatement(query); //Put your query in the quotes
		sampleQueryStatement.executeUpdate(); //Executes the query
		sampleQueryStatement.close(); //Closes the query
		conn.close(); //Closes the connection
	}
}

