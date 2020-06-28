package cc.wanforme.nukkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import cn.nukkit.Nukkit;
import cn.nukkit.Server;

public class NukkitApplicationLauncher implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(NukkitApplicationLauncher.class);
	
	@Override
	public void run(String... args) throws Exception {
		runNukkit(args);
	}

	public void runNukkit(String... args) {
		Runnable nukkitTask = new Runnable() {
			public void run() {
				Nukkit.main(args);
			}
		};
		
		Thread t = new Thread(nukkitTask);
		log.info("Running nukkit server.");
		t.start();
//		t.join();
	}
	
	public void stopNukkit() {
		log.info("Shut down nukkit server.");
		Server.getInstance().shutdown();
	}
	
}
