import java.io.File;
import java.io.IOException;

import cc.wanforme.nukkit.spring.util.ResourceSaver;

/**
 * @author wanne
 *
 *         2020年7月29日
 */
public class TestResourceSaver {

	public static void main(String[] args) throws IOException {
	//	String s = "G:\\Minecraft\\Nukkit\\ns_test\\nsplugins\\CMLogin-1.0-SNAPSHOT.jar";
	//	System.out.println(s.replace('\\', '/'));
	//	File source = new File("G:\\Minecraft\\Nukkit\\ns_test\\nsplugins\\CMLogin-1.0-SNAPSHOT.jar");
	//	File out = new File("G:\\Minecraft\\Nukkit\\ns_test\\nsplugins\\CMLoginn\\");
	//	ResourceSaver.saveZipResources(source, out, "lang", null, false);

		File source = new File(
				"D:\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\StarterDemo\\StarterDemo-1.0-SNAPSHOT.jar");
		File out = new File("D:\\eclipse-jee-2019-12-R-win32-x86_64\\workspace\\StarterDemo\\StarterDemo");
		ResourceSaver.saveBootResources(source, out, "config", false);
		System.out.println("finished!");
	}

}
