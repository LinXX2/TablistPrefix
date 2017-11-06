package me.linxx.tablistprefix.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private File file;
	private YamlConfiguration cfg;
	
	private PrefixManager pm;

	@Override
	public void onEnable() {
		instance = this;
		pm = new PrefixManager();
		register();
		try {
			loadFile();
		} catch (IOException | InvalidConfigurationException e) {e.printStackTrace();}
		pm.loadPrefixes();
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MainTick(), 5*60*20, 5*60*20);
	}
	
	private void loadFile() throws FileNotFoundException, IOException, InvalidConfigurationException {
		file = new File( getDataFolder().getPath(), "config.yml" );
		if( !file.exists() )
			saveResource( "config.yml", true );
		cfg = new YamlConfiguration();
		cfg.load(file);
	}
	
	private void register() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents( this.pm, this );
	}
	
	public Set< String > getRanks() {
		return cfg.getConfigurationSection( "TabRanks" ).getKeys( false );
	}
	
	public String getList( String path ) {
		String output = "";
		
		for( String s : cfg.getStringList( "TabDeco." + path ) ) {
			output += s.replace( "&", "§" ) + "\n";
		}
		if( output.length() > 0 ) output = output.substring( 0, output.length() -1 );
		
		return output
				.replace( "%onlineplayers%", Bukkit.getOnlinePlayers().size()+"" )
				.replace( "%maxplayers%", Bukkit.getMaxPlayers()+"" );
	}
	
	@SuppressWarnings("deprecation")
	public String getRank( Player t ) {
		return PermissionsEx.getUser( t ).getGroups()[ 0 ].getName();
	}
	
	public String getPrefix( String rank ) {
		return cfg.getString( "TabRanks." + rank ).replace( "&", "§" );
	}
	
	public PrefixManager getPM() {
		return pm;
	}
	
	public static Main getInstance() {
		return instance;
	}
}
