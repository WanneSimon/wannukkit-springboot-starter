package cc.wanforme.nukkit.plugins;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.configuration.NukkitSpringProperties;

/** 上下文辅助类<br>
 * 如果启用 nukkit，那么还将 创建一个新的 context 加载插件
 * @author wanne
 * 2020年7月7日
 */
@Component
public class NukkitApplicationContext implements ApplicationContextAware{
	
	@Autowired
	private NukkitSpringProperties properties;
	
	// 当前的 context 
	private ApplicationContext context;
	// 读取插件的 context
	private ApplicationContext pluginContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
	
	
	/** 读取插件nsplugins
	 */
	public void loadPlugins() {
		// 检查nukkit是否启用，并且启动了nukkit
		// 注：不检查 nukkit 是否启动
		if(!properties.isEnable()) {
			return ;
		}
		
		// 
		
	}
	
	
	/** 获取当前的 context，*/
	public ApplicationContext getApplicationContext() {
		return context;
	}
	
	/** 获取当插件的 context，*/
	public ApplicationContext getPluginApplicationContext() {
		return pluginContext;
	}
	
}
