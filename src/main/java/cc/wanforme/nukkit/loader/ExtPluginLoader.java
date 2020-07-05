package cc.wanforme.nukkit.loader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginDescription;
import cn.nukkit.plugin.PluginLoader;

/**
 * @author wanne
 * 2020年7月3日
 */
public class ExtPluginLoader implements PluginLoader{

	// 
	private final Map<String, Class> globalClasses = new HashMap<>();
	
	@Override
	public Plugin loadPlugin(String filename) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginDescription getPluginDescription(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginDescription getPluginDescription(File file) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pattern[] getPluginFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		// TODO Auto-generated method stub
		
	}


	void setGlobalClass(String name, Class<?> result) {
		globalClasses.put(name, result);
	}

	Class<?> getGlobalClassByName(String name) {
		return globalClasses.get(name);
	}
}
