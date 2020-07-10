package cc.wanforme.nukkit.spring.util;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginManager;

/**
 * @author wanne
 * 2020年7月10日
 */
public class NukkitServerUtil {

	/** nukkit 启动后，获取 server实例*/
	public static Server getServer() {
		return Server.getInstance();
	}
	
	
	/** nukkit 启动后，获取 PluginManager实例*/
	public static PluginManager getPluginManager() {
		Server server = getServer();
		if(server != null) {
			return server.getPluginManager();
		}
		return null;
	}
	
}
