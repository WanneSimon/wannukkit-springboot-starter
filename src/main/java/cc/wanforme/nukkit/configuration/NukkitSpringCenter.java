package cc.wanforme.nukkit.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cc.wanforme.nukkit.NukkitApplicationLauncher;
import cn.nukkit.Nukkit;
import cn.nukkit.Server;

/**
 * @author wanne
 * 2020年6月28日
 */
@Configuration
@EnableConfigurationProperties(NukkitSpringProperties.class)
@ConditionalOnProperty(
	prefix = "nukkit.spring",
	name = "enable",
	havingValue = "true"
)
public class NukkitSpringCenter implements CommandLineRunner{
	private static final Logger log = LoggerFactory.getLogger(NukkitApplicationLauncher.class);
	@Autowired
	private NukkitSpringProperties properties;
	
	@Override
	public void run(String... args) throws Exception {
		log.info(properties.toString());
		
		if(properties.isEnable()) {
			if(properties.isStartNukkit()) {
				runNukkit(args);
			}
		}
	}

	public void runNukkit(String... args) {
		Runnable nukkitTask = new Runnable() {
			public void run() {
				Nukkit.main(args);
			}
		};
		
		Thread t = new Thread(nukkitTask);
		log.info("Starting nukkit server.");
		t.start();
		// 非 web 应用，需要等待启动 nukkit 并阻塞当前线程
		// normal application needs to block current thread.
//		t.join(); 
	}
	
	public void stopNukkit() {
		log.info("Shutting down nukkit server.");
		Server.getInstance().shutdown();
	}
	
}
