package cc.wanforme.nukkit.util;

import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wanne
 *
 * 2020年7月6日
 */
@Component
public class SpringContextHolder implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(SpringContextHolder.class);
	
	private ApplicationContext context;
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.context = applicationContext;
	}

	/** 检查是否有Spring的注解<br>
	 * Component、Repository、Service、Configuration
	 * */
	public boolean isSpringBeanClass(Class<?> clazz) {
		if(clazz == null) {
			return false;
		}
		
		// 接口和抽象类
		if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			return false;
		}
		
        if(clazz.getAnnotation(Component.class)!=null){
            return true;
        }
        if(clazz.getAnnotation(Repository.class)!=null){
            return true;
        }
        if(clazz.getAnnotation(Service.class)!=null){
            return true;
        }
        if(clazz.getAnnotation(Configuration.class)!=null){
            return true;
        }
        
		return false;
	}
	
	/** 初始化，并注册bean*/
	public void registerBean(Class<?> clazz) {
		if(isSpringBeanClass(clazz)){
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
            //设置当前bean定义对象是单利的
            beanDefinition.setScope("singleton");

            //将变量首字母置小写
            String className = clazz.getCanonicalName();
            String beanName = StringUtils.uncapitalize(className);

            beanName =  beanName.substring(beanName.lastIndexOf(".")+1);
            beanName = StringUtils.uncapitalize(beanName);

//            context.getBeanFactory().registerBeanDefinition(beanName,beanDefinition);
//            context.
//            registeredBean.add(beanName);
        }
		
	}
	
}
