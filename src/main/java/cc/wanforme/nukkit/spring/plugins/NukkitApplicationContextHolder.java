package cc.wanforme.nukkit.spring.plugins;

import java.io.File;
import java.lang.reflect.Field;
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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.spring.configuration.NukkitSpringProperties;
import cc.wanforme.nukkit.spring.configuration.started.NukkitStartHandler;
import cc.wanforme.nukkit.spring.loader.ExtBeanClassLoader;
import cc.wanforme.nukkit.spring.loader.ExtPluginLoader;
import cc.wanforme.nukkit.spring.loader.ExtResourceLoader;
import cc.wanforme.nukkit.spring.util.NukkitServerUtil;
import cc.wanforme.nukkit.spring.util.PathResource;
import cn.nukkit.command.Command;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.registry.CommandRegistry;

/** 上下文辅助类<br>
 * 如果启用 nukkit，那么还将 创建一个新的 context 加载插件
 * @author wanne
 * 2020年7月7日
 */
@Component
public class NukkitApplicationContextHolder implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(NukkitApplicationContextHolder.class);
	public static final String EXT_PLUGIN_LOADER = "extPluginLoader";
	
	
	@Autowired
	private NukkitSpringProperties properties;
	@Autowired
	private NukkitStartHandler nukkitStartHandler;
	@Autowired
	private ExtPluginLoader pluginLoader;
	
	// 当前SpringBoot的 context 
	private ApplicationContext context;
	// 读取插件的 context
	private AnnotationConfigApplicationContext pluginContext;
	// 插件加载器（默认，设置SpringBoot#applicationContext时直接加入）
	private Map<String, PluginLoader> pluginLoaders = new HashMap<>(1);

	// 初始化pluginContext时（前）读取到的 PluginDescription (key是对应的插件文件)
	private Map<File, PluginDescription> pluginDescriptions = new HashMap<>();
	
	// 插件类加载器 (pluginContext初始化前)
	private ExtBeanClassLoader classLoader;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
		pluginLoaders.put(EXT_PLUGIN_LOADER, pluginLoader);
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
		classLoader = this.loadClasses();
		
		if(classLoader != null && nukkitStartHandler.isNukkitStarted()) {
			List<File> fs = this.urlToFile(classLoader.getURLs());
			
			// 初始化 PluginContext, 并预先读取所有插件的 PluginDescription
			this.initPluginContext(fs);
			
			// 加载插件
			List<Plugin> plugins = this.loadPlugins(fs);
			
			// 启用插件
			this.enablePlugins(plugins);;
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
	
	/** 获取某个插件文件的 PluginDescription*/
	public PluginDescription getPluginDescription(File file){
		return pluginDescriptions.get(file);
	}
	
	/** 获取外部插件的class*/
	public Class<?> getPluginClass(String name){
		if(classLoader!=null && name != null) {
			try {
				return classLoader.loadClass(name);
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
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
		URL[] uris = fs.stream().filter(f -> PathResource.isJarOrDirectory(f)).map(File::toURI).map(t -> {
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
	
	
	/** 初始化插件加载器的 context(并没有指定父容器)*/
	private void initPluginContext(final List<File> fs){
		// 预先读取 pluginDescription， 并从中找出主类的所在位置
		//（也就是说，主类必须在整个包的最外层目录下）
		// TODO... 待优化
		List<String> basePackage = new ArrayList<>(fs.size());
		for (File file : fs) {
			PluginDescription des = ExtPluginLoader.readPluginDescription(file);
			if(des != null) {
				pluginDescriptions.put(file, des);
				
				String main = des.getMain();
				int lastDot = main.lastIndexOf('.');
				
				// 使用默认包的情况 
				// TODO 仍然需要重新处理
				if(lastDot == -1) {
					log.warn(" Plugin - [{}] use default package, that's deprecated!");
					main = "";
				} else {
					main = main.substring(0, lastDot);
				}
				
				basePackage.add(main);
			}
		}
		
		// 扫描所有包
//		pluginContext = new AnnotationConfigApplicationContext(
//				(DefaultListableBeanFactory) context.getAutowireCapableBeanFactory());
//		pluginContext.scan(basePackage.toArray(new String[0]));
		
//		pluginContext = new AnnotationConfigApplicationContext(basePackage.toArray(new String[0]));
		pluginContext = new AnnotationConfigApplicationContext();
		pluginContext.setClassLoader(classLoader);
		// 参考 DefaultResourceLoader
		pluginContext.setResourceLoader(new ExtResourceLoader(classLoader));
		pluginContext.scan(basePackage.toArray(new String[0]));
		pluginContext.refresh();
	}
	
	/** 真正加载插件的地方*/ 
	private List<Plugin> loadPlugins(List<File> fs) {
		// 获取 PluginManager
		PluginManager pluginManager = NukkitServerUtil.getPluginManager();
		List<Plugin> list= new ArrayList<>(fs.size());
		if (pluginManager != null) {
			int tryCounter = 0; // 加载插件重试次数
			boolean isCommandClosed = false; //读取插件过程中， CommandRegistry 是否关闭
			for (int i = 0; i < fs.size(); i++) {
				File f = fs.get(i);
				
				// 我真不想加下面这句代码，但异常被PluginManager捕获，又没得办法
				if(CommandRegistry.get().isClosed()) {
					isCommandClosed = true;
					this.openCommandRegistry();
				}
				
				Plugin plugin = pluginManager.loadPlugin(f, pluginLoaders);
				if (plugin != null) {
					list.add(plugin);
					
					if(tryCounter > 0) {
						log.info("Commond Registry opened success");
						tryCounter = 0;
					}
					
				} 
				else if (CommandRegistry.get().isClosed()) {
					isCommandClosed = true;
					if (tryCounter >= 0 && tryCounter < 3) {
						i--; // 回滚下标，重试
						tryCounter++;
						log.info("Commond Registry closed! trying to open {}...", tryCounter);
						if (!this.openCommandRegistry()) { // 如果 CommandRegistry 打开失败！
							i++;
						} else {

						}
					} else if (tryCounter >=3) {
						log.info("Commond Registry can't be opened!");
						tryCounter = 0;
					}
				}

			}
			
			if(isCommandClosed) {
				CommandRegistry.get().close();
			}
		} else {
			log.warn("It seems the server is not started, during loading plugins!");
		}
		return list;
	}
	
	/** 启用插件*/
	private void enablePlugins(List<Plugin> plugins) {
		PluginManager pluginManager = NukkitServerUtil.getPluginManager();
		for (Plugin plugin : plugins) {
			pluginManager.enablePlugin(plugin);
		}
	}
	
	private List<File> urlToFile(URL[] urls) {
		List<File> fs = new ArrayList<>(urls.length);

		for (int i = 0; i < urls.length; i++) {
			try {
				fs.add(new File(urls[i].toURI()));
			} catch (URISyntaxException e) {
				log.error("Error occured while loading plugin [" + urls[i] + "]", e);
			}
		}
		return fs;
	}

	/** 反射打开 CommandRegistry, 返回是否成功打开*/
	private boolean openCommandRegistry() {
		try {
			openCommandRegistry(false);
			return true;
		} catch (Exception e) {
			log.info("open failed!", e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void openCommandRegistry(boolean isClose) throws Exception {
		CommandRegistry obj = CommandRegistry.get();
		Class<CommandRegistry> clazz = CommandRegistry.class;
		
		Field field = clazz.getDeclaredField("closed");
		boolean access = field.isAccessible();
		if(!access) {
			field.setAccessible(true);
		}
		field.set(obj, isClose);
		field.setAccessible(access);
		
		// CommandRegistry 关闭之后, 命令不可增删改。 registeredCommands,knownAliases
		Field registeredCommands = clazz.getDeclaredField("registeredCommands");
		access = registeredCommands.isAccessible();
		if(!access) {
			registeredCommands.setAccessible(true);
		}
		Map<String, Command> value1 = (Map<String, Command>)registeredCommands.get(obj);
		registeredCommands.set(obj, new HashMap<>(value1));
		registeredCommands.setAccessible(access);
		
		Field knownAliases = clazz.getDeclaredField("knownAliases");
		access = knownAliases.isAccessible();
		if(!access) {
			knownAliases.setAccessible(true);
		}
		Map<String, String> value2 = (Map<String, String>)knownAliases.get(obj);
		knownAliases.set(obj, new HashMap<>(value2));
		knownAliases.setAccessible(access);
	}
	
}
