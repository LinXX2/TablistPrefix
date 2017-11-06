package me.linxx.tablistprefix.main;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class PrefixManager implements Listener {

	private Map< String, String > prefixes = new HashMap<>();
	private Map< String, String > ids = new HashMap<>();

	@EventHandler
	public void onJoin( PlayerJoinEvent event ) {
		updateAll();

	}

	@EventHandler
	public void onQuit( PlayerQuitEvent event ) {
		updateAll();
	}

	public void loadPrefixes() {
		int id = 1;
		for ( String rank : Main.getInstance().getRanks() ) {
			String prefix = Main.getInstance().getPrefix( rank );
			prefixes.put( rank, prefix );
			ids.put( rank, String.format( "%02d", id ) );
			id++;
		}
		Bukkit.getLogger().info( "[Tablist] Prefixes loaded successfully!" );
		updateAll();
	}

	public void updateAll() {
		sendTabPackets( 1 );
		sendTabPackets( 0 );
	}

	private void sendTabPackets( int state ) {
		for ( String rankName : prefixes.keySet() ) {

			String prefix = prefixes.get( rankName );
			String id = ids.get( rankName );

			Collection< String > entrys = new ArrayList<>();
			for ( Player t : Bukkit.getOnlinePlayers() ) {
				String targetRank = Main.getInstance().getRank( t );
				if ( targetRank.equals( rankName ) ) {
					entrys.add( t.getName() );
				}
			}
			if ( rankName.length() > 14 )
				rankName = rankName.substring( 0, 14 );
			if ( prefix.length() > 14 )
				prefix = prefix.substring( 0, 14 );

			try {
				String teamName = id + rankName;

				Constructor< ? > constructor = getNMSClass( "PacketPlayOutScoreboardTeam" ).getConstructor();
				Object packet = constructor.newInstance();

				setField( packet, "a", teamName );
				setField( packet, "b", teamName );
				setField( packet, "c", prefix );
				setField( packet, "d", "" );
				setField( packet, "e", "ALWAYS" );
				setField( packet, "h", state );
				setField( packet, "g", entrys );

				for ( Player t : Bukkit.getOnlinePlayers() )
					sendPacket( t, packet );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	private Class< ? > getNMSClass( String name ) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split( "\\." )[ 3 ];
		try {
			return Class.forName( "net.minecraft.server." + version + "." + name );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendPacket( Player to, Object packet ) {
		try {
			Object playerHandle = to.getClass().getMethod( "getHandle" ).invoke( to );
			Object playerConnection = playerHandle.getClass().getField( "playerConnection" ).get( playerHandle );
			playerConnection.getClass().getMethod( "sendPacket", getNMSClass( "Packet" ) ).invoke( playerConnection,
					packet );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private void setField( Object change, String name, Object to ) {
		try {
			Field field = change.getClass().getDeclaredField( name );
			field.setAccessible( true );
			field.set( change, to );
			field.setAccessible( false );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}