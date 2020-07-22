package cc.wanforme.nukkit.spring.plugins.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/** 固定长度的指令处理器,动态指令使用 {{}} 进行包裹<br>
 * 例如：<br>
 * 完全固定的指令：/cm test <br>
 * 部分是动态的指令： /cm send {{player}} <br>
 * @author wanne
 * 2020年7月22日
 */
public abstract class FixedArgumentsHandler implements Comparable<Integer>{
	// 相同
	public static final int EQUAL = 0;
	// 不同
	public static final int DIFFERRENT = -1;
	// 其它正数表示，相似
	
	/** 参数部分
	 * 例如："/cm send {{name}} {{msg}}", args 就是 "send {{name}} {{msg}}" 这部分 , "cm" 就是 main
	 */
	
//	private String main; // main没啥用
	private String[] args ;
	// 替代符
	private Pattern p = null;
	
//	public FixedArgumentsHandler(String main, String[] args) {
//		this.main = main;
//		this.args = args;
//		p =  Pattern.compile("\\{\\{.*\\}\\}");
//	}
	public FixedArgumentsHandler(String[] args) {
		this.args = args;
		p =  Pattern.compile("\\{\\{.*\\}\\}");
	}
	
	public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args) ;
	
	public String[] getArgs() {
		return args;
	}
	
	/** 0 - 相同<br>
	 *  -1 - 不同<br>
	 *  其它正数 - 第一个不相同的下标，（相似）*/
	public int similarityWith(String[] arguments) {
		if(args == null && arguments==null) {
			return EQUAL;
		} else if(args == null || arguments==null) {
			return DIFFERRENT;
		}
		
		if(args.length != arguments.length) {
			return DIFFERRENT;
		}
		
		for(int i=0; i<args.length; i++) {
			Matcher matcher = p.matcher(args[i]);
			// 是占位符直接跳过，不是占位符必须要相等
			if(!matcher.matches()) {
				if(!args[i].equals(arguments[i])) {
					return i == 0 ? DIFFERRENT : i;
				}
			}
		}
		
		return EQUAL;
	}
	
	public static void main(String[] args) {
		Pattern p =  Pattern.compile("\\{\\{.*\\}\\}");
		
		String[] arr = { "{{abb}}", "aa{{bb}cc", "aaa{{}}", "{{}}bb",
				"aa{{bb}}", "aa{{}}cc", "{{bb}}cc", "{{cb}}", "{{}}"
		};
		
		for (String s : arr) {
			if(p.matcher(s).matches()) {
				System.out.println(s);
			}
		}
	}

	/** 排序时使用的权重,暂未实现,权重小的先判断*/
	@Override
	public int compareTo(Integer o) {
		return 0;
	}
	
}
