package cc.wanforme.nukkit.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.plugin.JavaPluginLoader;

/**
 * @author wanne
 * 2020年6月29日
 */
public class ExtBeanClassLoader extends URLClassLoader{

	// 保存插件加载器印用，检查所有从外部载入的类
	private ExtPluginLoader loader;
	private Map<String, Class<?>> loadedClasses = new HashMap<>();
	
	public ExtBeanClassLoader(URL[] urls, ClassLoader parent,  JavaPluginLoader loader) {
		super(urls);
	}

    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	// 遵循原规则
        if (name.startsWith("cn.nukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = loadedClasses.get(name);

        if (result == null) {
        	// 查询全局 class 
            result = loader.getGlobalClassByName(name);

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                	// 添加全局 class
                    loader.setGlobalClass(name, result);
                }
            }

            loadedClasses.put(name, result);
        }

        return result;
    }
	
	
	
}
