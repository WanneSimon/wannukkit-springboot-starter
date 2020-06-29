package cc.wanforme.nukkit.configuration.started;


import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.jline.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.configuration.NukkitSpringProperties;
import cc.wanforme.nukkit.util.ResourceSaver;
import cn.nukkit.Nukkit;
import cn.nukkit.Server;

/** nukkit 启动后的处理
 * @author wanne
 * 2020年6月28日
 */
@Component
public class NukkitStartHandler {
	private static final Logger log = LoggerFactory.getLogger(NukkitStartHandler.class);
	/** nukkit启动前，内置的必须要保存到外部的文件*/ 
	private static final String[] preSavingFiles = { "plugins/PMPlus.jar" }; 
	
	@Autowired
	private NukkitSpringProperties properties;
	
	private Thread subThread = null;
	private boolean nukkitStarted = false;
	
	protected void config() {
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
			}
		} catch (InterruptedException e) {
//			e.printStackTrace();
			log.error(e.getMessage(), e);
		} catch (ExecutionException e) {
//			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	/** nukkit 启动前的处理*/
	protected void beforeNukkitStart() {
		// 预保存starter中的文件
//		String pm = "plugins/PMPlus.jar";
		for (String f : preSavingFiles) {
			try {
				ResourceSaver.saveInnerResource(f, ResourceSaver.appLocation + "/" + f, true);
			} catch (IOException e1) {
				log.error("PMPlus saving error! ", e1);
			}
		}
		
		// 保存项目内部资源到外部
		if(properties.getSavingFilesBeforeNukkit() != null) {
			for (String innerFolder : properties.getSavingFilesBeforeNukkit()) {
				try {
					ResourceSaver.saveInnerFile(innerFolder, true);
				} catch (IOException e) {
					log.error("saving resources-folder error ["+innerFolder+"]", e);
				}
			}
		}
		
		
	}
	
	/** nukkit 启动成功后的处理，读取各个地方的插件*/
	protected void afterNukkitStarted() {
		
	}
	
	
	public void runNukkit(String... args) {
		this.beforeNukkitStart();
		
		Runnable nukkitTask = new Runnable() {
			public void run() {
				log.info("Starting nukkit server.");
				Nukkit.main(args);
			}
		};
		
		Thread t = new Thread(nukkitTask);
		t.start();
		
		this.config();
		this.afterNukkitStarted();
		// 非 web 应用，需要等待启动 nukkit 并阻塞当前线程
		// normal application needs to block current thread.
//		t.join(); 
	}
	
	public void stopNukkit() {
		log.info("Shutting down nukkit server.");
		Server.getInstance().shutdown();
		this.setNukkitStarted(false);
	}
	
	/** nukkit是否启动成功了*/
	public boolean isNukkitStarted() {
		return nukkitStarted;
	}
	
	private void setNukkitStarted(boolean nukkitStarted) {
		this.nukkitStarted = nukkitStarted;
	}
	
}
