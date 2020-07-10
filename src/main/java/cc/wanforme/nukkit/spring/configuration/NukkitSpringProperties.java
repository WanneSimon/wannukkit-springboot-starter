package cc.wanforme.nukkit.spring.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wanne
 * 2020年6月28日
 */
@ConfigurationProperties(prefix = "nukkit.spring")
public class NukkitSpringProperties {
	/** 是否启用 nukkit */
	private boolean enable = true;
	/** 是否一同启动 nukkit*/
	private boolean startNukkit=true;
	/** 使用spring写的nukkit插件在哪个目录下*/
	private String nukkitSpringPluginLocation = "nsplugins";
	
	/** 监听 nukkit 启动的间隔（ms）*/
	private int listenTick = 50;
	/** 监听 nukkit 启动的超时时间（ms）*/
	private int listenTimeOut = 6000;
	/** 需要在 nukkit 启动前保存到外部的文件夹*/
	private String[] savingFilesBeforeNukkit = { "plugins" };
	
	public boolean isEnable() {
		return enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public boolean isStartNukkit() {
		return startNukkit;
	}
	public void setStartNukkit(boolean startNukkit) {
		this.startNukkit = startNukkit;
	}
	
	public String getNukkitSpringPluginLocation() {
		return nukkitSpringPluginLocation;
	}
	public void setNukkitSpringPluginLocation(String nukkitSpringPluginLocation) {
		this.nukkitSpringPluginLocation = nukkitSpringPluginLocation;
	}
	
	public int getListenTick() {
		return listenTick;
	}
	public void setListenTick(int listenTick) {
		this.listenTick = listenTick;
	}
	
	public int getListenTimeOut() {
		return listenTimeOut;
	}
	public void setListenTimeOut(int listenTimeOut) {
		this.listenTimeOut = listenTimeOut;
	}
	
	public String[] getSavingFilesBeforeNukkit() {
		return savingFilesBeforeNukkit;
	}
	public void setSavingFilesBeforeNukkit(String[] savingFilesBeforeNukkit) {
		this.savingFilesBeforeNukkit = savingFilesBeforeNukkit;
	}
	
}
