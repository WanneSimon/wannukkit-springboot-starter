package cc.wanforme.nukkit.configuration;

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

	@Override
	public String toString() {
		return "NukkitSpringProperties [enable=" + enable + ", startNukkit=" + startNukkit
				+ ", nukkitSpringPluginLocation=" + nukkitSpringPluginLocation + "]";
	}
}
