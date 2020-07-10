package cc.wanforme.nukkit.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;
import cn.nukkit.utils.Utils;

/**
 * @author wanne
 * 2020年7月3日
 */
public class ExtPluginLoader implements PluginLoader{
	// 
//	private final Map<String, Class> globalClasses = new HashMap<>();
	
	@Override
	public Plugin loadPlugin(String filename) throws Exception {
		return this.loadPlugin(new File(filename));
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		
		return null;
	}

	@Override
	public PluginDescription getPluginDescription(String filename) {
		return this.getPluginDescription(new File(filename));
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
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
		
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		
	}


//	void setGlobalClass(String name, Class<?> result) {
//		globalClasses.put(name, result);
//	}
//
//	Class<?> getGlobalClassByName(String name) {
//		return globalClasses.get(name);
//	}
	
}
