package cc.wanforme.nukkit.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.configuration.NukkitSpringProperties;
import cc.wanforme.nukkit.configuration.started.NukkitStartHandler;
import cc.wanforme.nukkit.loader.ExtBeanClassLoader;
import cc.wanforme.nukkit.loader.ExtPluginLoader;
import cc.wanforme.nukkit.util.NukkitServerUtil;
import cc.wanforme.nukkit.util.PathResource;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.plugin.PluginManager;

/** 上下文辅助类<br>
 * 如果启用 nukkit，那么还将 创建一个新的 context 加载插件
 * @author wanne
 * 2020年7月7日
 */
@Component
public class NukkitApplicationContext implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(NukkitApplicationContext.class);
	public static final String EXT_PLUGIN_LOADER = "extPluginLoader";
	
	
	@Autowired
	private NukkitSpringProperties properties;
	@Autowired
	private NukkitStartHandler nukkitStartHandler;
	
	// 当前的 context 
	private ApplicationContext context;
	// 读取插件的 context
	private ApplicationContext pluginContext;
	// 插件加载器
	private Map<String, PluginLoader> pluginLoaders = new HashMap<>(1);

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		pluginLoaders.put(EXT_PLUGIN_LOADER, new ExtPluginLoader());
	}
	
	
	/** 读取插件nsplugins, 开放给外部直接调用
	 */
	public void loadPlugins() {
		// 检查nukkit是否启用，并且启动了nukkit
		// 注：不检查 nukkit 是否启动
		if(!properties.isEnable()) {
			return ;
		}
		
		// 加载插件的所有类
		ExtBeanClassLoader classLoader = this.loadClasses();
		
		if(classLoader != null) {
			this.loadPlugins(classLoader);
		} else {
			log.warn("There's no plugin, or error occured while loading plugins!");
		}
		
	}
	
	/** 获取当前SpringBoot的 context，*/
	public ApplicationContext getApplicationContext() {
		return context;
	}
	
	/** 获取当前加载插件的 context，*/
	public ApplicationContext getPluginApplicationContext() {
		return pluginContext;
	}
	
	/** 加载插件的所有类,并返回类加载器*/
	private ExtBeanClassLoader loadClasses() {
		// 获取项目路径，然后获取到插件路径
		File projectLocation = PathResource.getProjectLocation();
		File pluginDir = new File(projectLocation, properties.getNukkitSpringPluginLocation());
		if (!pluginDir.exists() || !pluginDir.isDirectory()) {
			pluginDir.mkdir();
		}

		// 获取所有的文件，并过滤文件类型
		// ExtPluginLoader 会再过滤一次文件类型
		File[] files = pluginDir.listFiles();
		List<File> fs = new ArrayList<>(Arrays.asList(files));
		URL[] uris = fs.stream().filter(f -> ExtPluginLoader.isJarOrDirectory(f)).map(File::toURI).map(t -> {
			try {
				return t.toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(e -> e != null).collect(Collectors.toList()).toArray(new URL[0]);

		if (uris.length > 0) {
			// 实例化类加载器，加载所有的类
			ExtBeanClassLoader classLoader = new ExtBeanClassLoader(uris,
					Thread.currentThread().getContextClassLoader());
			return classLoader;
		}
		return null;
	}
	
	/** 插件类加载实例化后,读取并加载插件*/
	private void loadPlugins(ExtBeanClassLoader classLoader) {
		// 检查 nukkit 运行状况
		if(nukkitStartHandler.isNukkitStarted()) {
			URL[] urls = classLoader.getURLs();
			List<File> fs = new ArrayList<>(urls.length);
			
			for (int i=0; i<urls.length; i++) {
				try {
					fs.add(new File(urls[i].toURI()));
				} catch (URISyntaxException e) {
					log.error("Error occured while loading plugin ["+urls[i]+"]", e);
				}
			}
			
			// 获取 PluginManager
			PluginManager pluginManager = NukkitServerUtil.getPluginManager();
			if(pluginManager != null) {
				for (File f : fs) {
					pluginManager.loadPlugin(f, pluginLoaders);
				}
			} else {
				log.warn("It seems the server is not started, during loading plugins!");
			}
		}
		
	}
	
	
	
}
