package cc.wanforme.nukkit.started;

import static org.assertj.core.api.Assertions.contentOf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.configuration.NukkitSpringProperties;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginManager;

/** nukkit 启动后的处理
 * @author wanne
 * 2020年6月28日
 */
@Component
public class NukkitStartedHandler implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(NukkitStartedHandler.class);
	
	private ApplicationContext context;
	
	@Autowired
	private NukkitSpringProperties properties;
	
	private Thread subThread = null;
	private boolean nukkitStarted = false;
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	
	public void config() {
		if(!properties.isEnable()) {
			Log.warn("nukkit is not enabled!");
			return;
		}
		
		// start a thread to listen nukkit start.
		NukkitLaunchListener task = new NukkitLaunchListener(properties);
		FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
		subThread = new Thread(futureTask);
		subThread.start();
		
		try {
			Integer integer = futureTask.get();
			if(integer != 1) {
				log.warn("It seems nukkit does not run correctly! please check it out!");
			}
			
			this.setNukkitStarted(true);
			this.afterNukkitStarted();
		} catch (InterruptedException e) {
//			e.printStackTrace();
			log.error(e.getMessage(), e);
		} catch (ExecutionException e) {
//			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	/** nukkit 启动成功后的处理*/
	protected void afterNukkitStarted() {
		// 注册到 spring容器 (server, pluginManager)
//		Server server = Server.getInstance();
//		AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//		beanFactory.autowireBean(server);
//		beanFactory.autowireBean(server.getPluginManager());
		
//		((AnnotationConfigServletWebServerApplicationContext)context)
//		System.out.println(context.getClass().getCanonicalName());
		
//		System.out.println("<after bean set!>");
//		System.out.println(context.getBean(Server.class));
//		System.out.println(context.getBean(PluginManager.class));
	}
	
	/** nukkit是否启动成功了*/
	public boolean isNukkitStarted() {
		return nukkitStarted;
	}
	
	public void setNukkitStarted(boolean nukkitStarted) {
		this.nukkitStarted = nukkitStarted;
	}
	
}
