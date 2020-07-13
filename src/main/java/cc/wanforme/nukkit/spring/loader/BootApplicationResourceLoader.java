package cc.wanforme.nukkit.spring.loader;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/** 未使用
 * @author wanne
 * 2020年7月13日
 */
public class BootApplicationResourceLoader extends DefaultResourceLoader{

	public BootApplicationResourceLoader() {
		super();
	}
	
	public BootApplicationResourceLoader(@Nullable ClassLoader classLoader) {
		super(classLoader);
	}

	
	@Override
	public Resource getResource(String location) {
//		System.out.println("self-class loader: " + location);
		return super.getResource(location);
	}
	
	@Override
	protected Resource getResourceByPath(String path) {
//		System.out.println("self-class loader path: " + path);
		return super.getResourceByPath(path);
	}
}
