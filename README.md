# wannukkit-springboot-starter
SpringBoot Web starter for nukkit

一款简单整合 nukkit 和 SpringBoot-web 的三方依赖



[开发Demo](https://github.com/WanneSimon/StarterDemo)，[nsplugin Demo](https://github.com/WanneSimon/StarterPluginDemo)

**（目前正处于开发测试阶段）**

为方便讲述，后面 nukkit-1.x 和 cloudburst 统称nukkit

### 功能

1. 启动 nukkit 和 一个web 程序。**Spring 不管理 nukkit**
2. 保持 nukkit 原生，基于 nukkit 的插件依然可以使用。
3. 是否启动 nukkit 是可选。
4. 基于 Spring 注解式的 nukkit 插件开发 -- nsplugin（现已支持指定多个路径）。
5. 内置原nukkit插件 -- [PMPlus](https://github.com/WanneSimon/PMPlus/tree/2.0/build)（注：已支持cloudburst）。
6. 支持在 nukkit 启动前保存内部资源到外部。例如预置插件。



stater内部已经内置了 PMPlus（现在还有个问题没有修复），程序运行时就会保存。

### 注意 

1. ​	项目使用自定义日志输出，会覆盖原 nukkit 的日志配置，并且会导致 颜色代码 失效。

2. **特别注意：使用 springboot 自己的打包插件，会导致 cloudburst 无法启动，但是对于 nukkit-1.x无影响(1.0分支的支持已经弃坑，很多功能无法使用)。**	

   ​	具体情况：无法读取 nukkit 内部的语言配置等文件。

   ​	原因：SpringBoot 不允许随意访问 jar 包内部的资源。

   ​	解决方式：更换打包方式。pom.xml中添加打包插件 -- spring-boot-thin-maven-plugin，配置”在编译时下载依赖包“ ，将程序和依赖分离（具体查看 [demo](https://github.com/WanneSimon/StarterDemo)）。另一种方式（待验证）：启动 SpringBootApplication 前，自定义 ResourceLoader。

3. cloudburst 已不再支持动态加载插件，PMPlus-2.0 没有解决这个问题。这个功能会被屏蔽，不予修复，保持 cloudburst 的规则。

### 其它

关于 nukkit 启动检测，使用的方法是依次检测 Server 实例和 PluginManager 实例。可以设置超时时间，不支持一直检测。



### 仓库地址

在 pom 中添加

	<repositories>
		<repository>
			<id>nukkitx-repo</id>
			<!-- <url>https://repo.nukkitx.com/snapshot/</url> -->
			<url>http://www.repo.wanforme.cc/repository/nukkit/</url>
		</repository>
	</repositories>
或者，

你也可以 wannukkit-stpringboot 来搭建项目了，项目搭建更简单。添加父项目，需要使用上面的仓库地址。

详细参考 [StarterDemo](https://github.com/WanneSimon/StarterDemo)（已更新）。

	<parent>
	  <groupId>cc.wanforme.nukkit</groupId>
	  <artifactId>wannnukkit-springboot</artifactId>
	  <version>1.0.0</version>
	</parent>
