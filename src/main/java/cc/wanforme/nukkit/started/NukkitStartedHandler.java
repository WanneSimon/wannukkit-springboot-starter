package cc.wanforme.nukkit.started;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.configuration.NukkitSpringProperties;

/** nukkit 启动后的处理
 * @author wanne
 * 2020年6月28日
 */
@Component
public class NukkitStartedHandler {
	private static final Logger log = LoggerFactory.getLogger(NukkitStartedHandler.class);
	
	@Autowired
	private NukkitSpringProperties properties;
	
	private Thread subThread = null;
	private boolean nukkitStarted = false;
	
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
			} else {
				// 一旦 NukkitLaunchListener 的任务结束，并且返回状态为1，那么就表示启动完成
				this.setNukkitStarted(true);
				this.afterNukkitStarted();
			}
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
		
	}
	
	/** nukkit是否启动成功了*/
	public boolean isNukkitStarted() {
		return nukkitStarted;
	}
	
	public void setNukkitStarted(boolean nukkitStarted) {
		this.nukkitStarted = nukkitStarted;
	}
	
}
