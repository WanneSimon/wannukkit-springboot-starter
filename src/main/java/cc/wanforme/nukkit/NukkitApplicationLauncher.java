package cc.wanforme.nukkit;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class NukkitApplicationLauncher {
	private static final Logger log = LoggerFactory.getLogger(NukkitApplicationLauncher.class);
	
	private static final String SERVER_SSL_KEY_STORE = "server.ssl.key-store";
	public static String serverFullAddress = ""; //
	
	public static void launchNukkit(Class<?> appClazz, String...args) {
		Environment env = SpringApplication.run(appClazz, args).getEnvironment();
		logApplicationStartup(env);
	}
	
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty(SERVER_SSL_KEY_STORE) != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtils.hasText(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        
        serverFullAddress = protocol + hostAddress + serverPort + contextPath;
        
        log.info("\n----------------------------------------------------------\n\t" +
                "'{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n" +
                "----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol, serverPort, contextPath,
            protocol, hostAddress, serverPort, contextPath,
            env.getActiveProfiles());
    }
    
    
}
