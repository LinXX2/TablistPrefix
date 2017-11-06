package me.linxx.tablistprefix.main;

public class MainTick implements Runnable {

	
	@Override
	public void run() {
		Main.getInstance().getPM().updateAll();
		Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), "pex reload");
		
	}

}
