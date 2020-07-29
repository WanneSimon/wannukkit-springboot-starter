package cc.wanforme.nukkit.spring.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import cn.nukkit.plugin.Plugin;

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
	
	/** 解压 zip文件中的某个文件夹到外部( 保存插件的内部资源)
	 * @param source zip 源文件
	 * @param outDir 输出的根目录
	 * @param dir 需要解压保存的路径 （例如插件内部的语言文件夹： configs/lang/）
	 * @param ignoreBaseDir 解析 zip 文件的时候，是否忽略某个基础路径（例如 SpringBoot 的 BOOT-INF）
	 * @param replace 是否覆盖已有的文件
	 * @throws IOException */
	public static void saveZipResources(File source, File outDir, String dir, String ignoreBaseDir, boolean replace) throws IOException {
		// 处理路径符号，方便后面处理
		dir = dir.replace('\\', '/');
		if(!dir.endsWith("/")) {
			dir += '/';
		}
		if(ignoreBaseDir != null) {
			ignoreBaseDir = ignoreBaseDir.replace('\\', '/');
			if(!ignoreBaseDir.endsWith("/")) {
				ignoreBaseDir += '/';
			}
		}
		
		try (ZipFile zFile = new ZipFile(source)){
			Enumeration<? extends ZipEntry> entries = zFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry en = entries.nextElement();
				// 在 zip 中的路径
				String enName = en.toString();
				// 临时判断用
				String tempName = enName; 
				
				// 1.忽略基础路径
				if(ignoreBaseDir != null && enName.startsWith(ignoreBaseDir)) {
					tempName = tempName.substring(ignoreBaseDir.length(), tempName.length());
				}
				
				// 2.判断是不是某个路径下的文件
				if( !tempName.equals(dir) && tempName.startsWith(dir) ) {
					// 3. 保存文件
					File f = new File(outDir, tempName);
					mkdirIfNotExisted(f.getParent());
					
					// 4. 检查是否覆盖文件
					if( f.exists() && !replace ) {
						continue;
					}
					
					// 5.保存文件
					try(InputStream fis = zFile.getInputStream(en);
						FileOutputStream fos = new FileOutputStream(f);){
						byte[] bs = new byte[10240];
						int len = 0;
						while( (len = fis.read(bs)) != -1 ) {
							fos.write(bs, 0, len);
						}
					}
				}
			}
		}
		
	}
	
	/** 解压 SpringBoot jar包中的某个文件夹到外部( 保存插件的内部资源)
	 * @param source zip 源文件
	 * @param outDir 输出的根目录
	 * @param dir 需要解压保存的路径 （例如插件内部的语言文件夹： configs/lang/）
	 * @param replace 是否覆盖已有的文件
	 * @throws IOException */
	public static void saveBootResources(File source, File outDir, String dir, boolean replace) throws IOException {
		saveZipResources(source, outDir, dir, "BOOT-INF/classes/", replace);
	}
	
	/** 解压 zip文件中的某个文件夹到外部( 保存插件的内部资源)
	 * @param source zip 源文件
	 * @param outDir 输出的根目录
	 * @param dir 需要解压保存的路径 （例如插件内部的语言文件夹： configs/lang/）
	 * @param replace 是否覆盖已有的文件
	 * @throws IOException */
	public static void saveZipResources(File source, File outDir, String dir, boolean replace) throws IOException {
		saveZipResources(source, outDir, dir, null, replace);
	}
	
	/** 解压插件中某个文件夹所有资源到外部( 保存插件的内部资源)
	 * @param source zip 源文件
	 * @param outDir 输出的根目录
	 * @param dir 需要解压保存的路径 （例如插件内部的语言文件夹： configs/lang/）
	 * @throws IOException */
	public static void savePluginResources(Plugin plugin, String dir) throws IOException{
		savePluginResources(plugin, dir, false);
	}
	
	/** 解压插件中某个文件夹所有资源到外部( 保存插件的内部资源)
	 * @param source zip 源文件
	 * @param outDir 输出的根目录
	 * @param dir 需要解压保存的路径 （例如插件内部的语言文件夹： configs/lang/）
	 * @param replace 是否覆盖已有的文件
	 * @throws IOException */
	public static void savePluginResources(Plugin plugin, String dir, boolean replace) throws IOException {
		// /G:/Minecraft/Nukkit/ns_test/nsplugins/CMLogin-1.0-SNAPSHOT.jar
		String f = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		File source = new File(f);
		File outDir = plugin.getDataFolder();

		// 保险起见，按 SpringBoot jar进行处理
		saveBootResources(source, outDir, dir, false);
	}
	
}
