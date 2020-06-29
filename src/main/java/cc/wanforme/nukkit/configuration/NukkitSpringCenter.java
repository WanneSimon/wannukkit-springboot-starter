package cc.wanforme.nukkit.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cc.wanforme.nukkit.NukkitApplicationLauncher;
import cc.wanforme.nukkit.configuration.started.NukkitStartHandler;

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
	@Autowired
	private NukkitStartHandler nukkitStartHandler;
	
	@Override
	public void run(String... args) throws Exception {
		log.info(properties.toString());
		
		if(properties.isEnable()) {
			if(properties.isStartNukkit()) {
				nukkitStartHandler.runNukkit(args);
			}
		}
	}

	
}
