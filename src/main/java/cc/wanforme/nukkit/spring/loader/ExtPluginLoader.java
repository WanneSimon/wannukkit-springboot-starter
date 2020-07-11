package cc.wanforme.nukkit.spring.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cc.wanforme.nukkit.spring.plugins.NukkitApplicationContextHolder;
import cc.wanforme.nukkit.spring.util.NukkitServerUtil;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;

/**
 * @author wanne
 * 2020年7月3日
 */
@Component
public class ExtPluginLoader implements PluginLoader{
	private static final Logger log = LoggerFactory.getLogger(ExtPluginLoader.class);
	
	@Autowired
	@Lazy
	private NukkitApplicationContextHolder contextHolder;
	
	// 
//	private final Map<String, Class> globalClasses = new HashMap<>();
	@Override
	public Plugin loadPlugin(String filename) throws Exception {
		return this.loadPlugin(new File(filename));
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
        PluginDescription description = this.getPluginDescription(file);
        if (description != null) {
        	log.info("load plugin " + description.getFullName());
            File dataFolder = new File(file.getParentFile(), description.getName());

            String main = description.getMain();
            Class<?> mainClazz = contextHolder.getPluginClass(main);
            Object bean = contextHolder.getPluginApplicationContext().getBean(mainClazz);
            
            PluginBase plugin=null;
            if(bean!=null) {
            	if(!PluginBase.class.isAssignableFrom(mainClazz)) {
            		throw new PluginException("Main class `" + description.getMain() + "' does not extend PluginBase");
            	}
            	
            	plugin = (PluginBase) bean;
                plugin.init(this, NukkitServerUtil.getServer(), description, dataFolder, file);
                plugin.onLoad();
            }
            return plugin;
        }
        return null;
    }

	@Override
	public PluginDescription getPluginDescription(String filename) {
		return this.getPluginDescription(new File(filename));
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
		// 先检查初始化context的时候是否有加载过
		PluginDescription des = contextHolder.getPluginDescription(file);
		if(des == null) {
			des = readPluginDescription(file);
		}
		return des;
	}
	
	public static PluginDescription readPluginDescription(File file) {
		// jar 类型
		if(file.getName().endsWith(".jar")) {
	        try (JarFile jar = new JarFile(file)) {
	            JarEntry entry = jar.getJarEntry("nukkit.yml");
	            if (entry == null) {
	                entry = jar.getJarEntry("plugin.yml");
	                if (entry == null) {
	                    return null;
	                }
	            }
	            try (InputStream stream = jar.getInputStream(entry)) {
	                return new PluginDescription(Utils.readFile(stream));
	            }
	        } catch (IOException e) {
	            return null;
	        }
		} else {
			// 文件夹类型
			File desFile = new File(file.getAbsolutePath()+"/nukkit.yml");
			if(!desFile.exists()) {
				desFile = new File(file.getAbsolutePath()+"/plugin.yml");
			}
			
			if(!desFile.exists()) {
				return null;
			}
			
			try (InputStream stream = new FileInputStream(desFile)) {
                return new PluginDescription(Utils.readFile(stream));
            } catch (IOException e) {
	            return null;
	        }
		}
	}
	
	// jar文件和文件夹（当然也包括没有后缀的文件）
	@Override
	public Pattern[] getPluginFilters() {
		return new Pattern[] {Pattern.compile("^.+")};
	}

	@Override
	public void enablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && !plugin.isEnabled()) {
        	log.info("enable plugin {}", plugin.getDescription().getFullName());
        	
            ((PluginBase) plugin).setEnabled(true);

            NukkitServerUtil.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

	@Override
	public void disablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && plugin.isEnabled()) {
        	log.info("disable plugin {}", plugin.getDescription().getFullName());

//            NukkitServerUtil.getPluginManager().disablePlugin(plugin);

            NukkitServerUtil.getPluginManager().callEvent(new PluginDisableEvent(plugin));

            ((PluginBase) plugin).setEnabled(false);
        }
    }


//	void setGlobalClass(String name, Class<?> result) {
//		globalClasses.put(name, result);
//	}
//
//	Class<?> getGlobalClassByName(String name) {
//		return globalClasses.get(name);
//	}
	
}
