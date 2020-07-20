package cc.wanforme.nukkit.spring.loader;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

/**for spring context
 * @author wanne
 *
 * 2020年7月11日
 */
public class ExtResourceLoader extends DefaultResourceLoader{

	public ExtResourceLoader(ExtBeanClassLoader classLoader) {
		super.setClassLoader(classLoader);
	}
	
	@Override
	public Resource getResource(String location) {
		Resource resource =  super.getResource(location);
		return resource;
	}
	
	@Override
	public ClassLoader getClassLoader() {
		return super.getClassLoader();
	}
	
}
