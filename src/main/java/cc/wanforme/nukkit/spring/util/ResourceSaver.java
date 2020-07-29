package cc.wanforme.nukkit.spring.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author wanne
 * 2020年5月26日
 * 
 */
public class ResourceSaver {
	private static final Logger log = LoggerFactory.getLogger(ResourceSaver.class);
	public static final String appLocation = System.getProperty("user.dir");
	
	/**保存工程内部的文件<br>
	 * save files (inner of project)
	 * @param sourcePath 只能是文件
	 * @param desFile 只能是文件
	 * @param override 是否覆盖 (override the old file)
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static boolean saveInnerResource(String relativeSourcePath, String desFile, boolean override) throws FileNotFoundException, IOException {
//		ClassPathResource resource = new ClassPathResource("lib/***");
		ClassPathResource resource = new ClassPathResource(relativeSourcePath);
		
		File file = new File(desFile);
		if(file.exists() && !override) {
			log.info(" resource \"" + file.getAbsolutePath() + "\" existed, skip !");
			return false;
		}
		
		log.info("[saving] " + relativeSourcePath + " > "+ desFile);
//		log.info("saving resource \"" + file.getAbsolutePath() + "\" ");
		mkdirIfNotExisted(file.getParentFile());
		
		try (InputStream is = resource.getInputStream();
			 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			){
			
			byte[] bs = new byte[10240];
			int len = 1;
			while ( (len=is.read(bs)) != -1) {
				bos.write(bs, 0, len);
			}
			return true;
		}
	}
	
	/** 保存应用内部某个文件夹到应用外部
	 * @param innerFile 可以是文件，也可以是分割符结尾的文件夹
	 * @param override 是否覆盖 (override the old file)
	 * @return
	 * @throws IOException 
	 */
	public static boolean saveInnerFile(String innerFile, boolean override) throws IOException {
		if(innerFile==null) {
			return false;
		}
		
		// 分割符结尾的是文件夹，否则就是单个文件
		
		// 保存单个文件
		if(!innerFile.endsWith("/") && !innerFile.endsWith(File.separator)) {
//			innerFile += File.separator;
			return ResourceSaver.saveInnerResource(innerFile, 
					ResourceSaver.appLocation + "/" + innerFile, override);
		}
		
		// 保存文件夹
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath:" + innerFile + "*");
		if(resources != null) {
			for (Resource resource : resources) {
				String filename = resource.getFilename();
				ResourceSaver.saveInnerResource(innerFile + filename, 
						appLocation + "/" + innerFile + filename, override);
			}
		}
		return true;
	}
	
	public static void mkdirIfNotExisted(String absolutePath) {
		mkdirIfNotExisted(new File(absolutePath));
	}
	
	public static void mkdirIfNotExisted(File dir) {
		if(dir != null && !dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
	}
	
	/** 保存插件的内部资源*/
	public static void saveNukkitPluginResources() {
		
	}
	
}
