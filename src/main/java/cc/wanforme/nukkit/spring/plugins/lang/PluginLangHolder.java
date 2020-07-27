package cc.wanforme.nukkit.spring.plugins.lang;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.wanforme.nukkit.spring.util.NukkitServerUtil;
import cn.nukkit.utils.Config;

/** 多语言容器<br>
 * 规则：所有语言文件放在一个 basePackage 下，该文件夹只能放语言文件<br>
 * 每个中语言类型对应一个文件。如： 'en' -> 'en.yml' (如果语言文件类型是yml)
 * 创建实例后，每个语言文件对应一个Config。
 * @author wanne
 * 2020年7月22日
 */
public abstract class PluginLangHolder {
	private static final Logger log = LoggerFactory.getLogger(PluginLangHolder.class);
	
	// 读取进来的语言，可能是所有的语言，也可能只有正在使用中的语言
	private Map<String, Config> langs ;
	// 文件类型
	private final ConfigFileType type;
	// 当前使用的语言
	private final String current;
	// 是否加载所有语言
//	private final boolean loadAllLangs;
	// 语言文件所在基础路径
	private final String basePackage;
	
	/** 只读取传入的语言。其它语言不读取。<br>
	 * 当传入的语言不存在时，默认查找 nukkit.yml 中的语言配置 settings.language
	 * @param type 语言类型 {@link cn.nukkit.utils.Config}
	 * @param basePackage
	 * @param lang 当前读取语言
	 */
	public PluginLangHolder(ConfigFileType type, String basePackage, String lang) {
		this(type, basePackage, lang, 
				NukkitServerUtil.getServer().getConfig("settings.language"), false);
	}
	
	/** 只读取传入的语言。其它语言不读取
	 * @param type 语言类型 {@link cn.nukkit.utils.Config}
	 * @param basePackage
	 * @param lang 当前读取语言
	 * @param defaultLang 默认语言
	 */
	public PluginLangHolder(ConfigFileType type, String basePackage, String lang, String defaultLang) {
		this(type, basePackage, lang, defaultLang, false);
	}
	
	/** 只读取传入的语言。其它语言不读取
	 * @param type 语言类型 {@link cn.nukkit.utils.Config}
	 * @param basePackage
	 * @param lang
	 * @param defaultLang 默认语言
	 * @param loadAll 是否将所有语言文件读入进来
	 */
	public PluginLangHolder(ConfigFileType type, String basePackage, String lang, String defaultLang, boolean loadAll) {
		this.type = type;
//		this.loadAllLangs = loadAll;
		this.current = lang;
		this.basePackage = basePackage;
		
		if(loadAll) {
			File[] fs = new File(basePackage).listFiles();
			if(fs == null || fs.length == 0) {
				throw new RuntimeException("There's no language files, please check base package '"+basePackage+"'");
			}
			
			for (File f : fs) {
				String name = f.getName();
				String lang_f = name;
				int index = name.lastIndexOf('.');
				if(index != -1) {
					lang_f = lang_f.substring(0, index);
				}
				this.loadLang(lang_f, f, type);
			}
		} else {
			File f = new File(basePackage, lang);
			if(!f.exists()) {
				throw new RuntimeException("There's no language file, please check it out '"+f.getAbsolutePath()+"'");
			}
			
			this.loadLang(lang, f, type);
		}
		
	}
	
	private Config loadLang(String lang, File f, ConfigFileType type) {
		try {
			Config c = new Config(f, type.getConfigCode());
			this.langs.put(lang, c);
			return c;
		} catch (Exception e) {
			log.error("load lang file ["+f.getAbsolutePath()+"] failed !", e);
		}
		return null;
	}
	
	/** 设置语言类型, 加载并设置成功时，返回语言文件，错误时返回null*/
	public Config setLang(String name) {
		Config c = langs.get(name);
		if(c == null) {
			c = this.loadLang(name, new File(basePackage, name+type.getType()), type);
		}
		return c;
	}

	/** 刷新，并返回当前的语言配置对象*/
	public Config refresh() {
		langs.keySet().forEach( e -> {
			Config lang = this.loadLang(e, new File(this.basePackage,  e+type.getType()), type);
			if(lang == null) {
				this.langs.remove(e);
			} else {
				this.langs.put(e, lang);
			}
		});
		
		return this.getCurrentConfig();
	}
	
	/** 获取语言文件的文件类型*/
	public ConfigFileType getType() {
		return type;
	}
	
	/** 获取当前语言明*/
	public String getCurrentLang() {
		return current;
	}

	/** 获取已加载的所有语言*/
	public Set<String> getLoadedLangs(){
		return langs.keySet();
	}
	
	/** 获取某个语言文件*/
	public Config getLangConfig(String lang) {
		return langs.get(lang);
	}
	
	/** 获取正在使用中的语言文件*/
	public Config getCurrentConfig() {
		return langs.get(current);
	}
	
	
	/** 获取文件中配置的语言*/
	public String get(String key) {
		return getCurrentConfig().getString(key);
	}
	/** 获取文件中配置的语言*/
	public String get(String key, String defaultValue) {
		return getCurrentConfig().getString(key, defaultValue);
	}
}
