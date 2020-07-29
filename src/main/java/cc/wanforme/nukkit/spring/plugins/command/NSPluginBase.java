package cc.wanforme.nukkit.spring.plugins.command;

import java.util.HashMap;
import java.util.Map;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

/**
 * 多主指令插件基础类
 * 
 * @author wanne 2020年7月22日
 */
public abstract class NSPluginBase extends PluginBase {
	
	/** 注册的所有指令，键是主指令*/
	protected Map<String, NSCommand> mainCommands = new HashMap<>();

	@Override
	public void onLoad() {
		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	/** 根据主命令去执行响应的指令*/
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		NSCommand multiCommandHandler = mainCommands.get(command.getName());
		if(multiCommandHandler!=null) {
			return multiCommandHandler.onCommand(sender, command, label, args);
		}
		return false;
	}
	
	/** 添加一个主指令处理器*/
	public void putMultiCommandHandler(String main, NSCommand mutilCommandHandler) {
		mainCommands.put(main, mutilCommandHandler);
	}
	
}
