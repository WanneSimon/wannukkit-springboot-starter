package cc.wanforme.nukkit.spring.plugins.lang;

import cn.nukkit.utils.Config;
/** {@link cn.nukkit.utils.Config} 类中，对文件类型的映射
 * @author wanne
 * 2020年7月22日
 */
public enum ConfigFileType {
	
//    public static final String DETECT = Config.DETECT+""; //Detect by file extension
//    public static final String PROPERTIES = Config.PROPERTIES+""; // .properties
//    public static final String CNF = Config.PROPERTIES+""; // .cnf
//    public static final String JSON = Config.JSON+""; // .js, .json
//    public static final String YAML = Config.YAML+""; // .yml, .yaml
//    public static final String ENUM = Config.ENUM+""; // .txt, .list, .enum
//    public static final String ENUMERATION = Config.ENUMERATION+"";
	
	PROPERTIES(".properties", Config.PROPERTIES),
	CNF(".cnf", Config.CNF),
	JSON(".json", Config.JSON),
	YML(".yml", Config.YAML),
	YAML(".yaml", Config.YAML),
	;
	
	private String type;
	private int configCode;
	
	private ConfigFileType(String type, int configCode){
		this.type = type;
		this.configCode = configCode;
	}
	
	public String getType() {
		return type;
	}
	public int getConfigCode() {
		return configCode;
	}
	
}
