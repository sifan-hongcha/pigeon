package com.hongcha.pigeon.spring;

import com.hongcha.pigeon.core.service.annotations.PigeonService;
import com.hongcha.pigeon.core.utils.ClassUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Set;


public class PigeonBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
    private Environment environment;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] scanPackages = environment.getRequiredProperty(PigeonSpringConstant.PREFIX + "." + PigeonSpringConstant.PACKAGES, String[].class);
        Set<Class<?>> classes = ClassUtil.getClasses(scanPackages);
        classes
                .stream()
                .filter(cl -> !cl.isInterface() && cl.isAnnotationPresent(PigeonService.class))
                .forEach(cl -> {
                    PigeonService annotation = cl.getAnnotation(PigeonService.class);
                    String group = annotation.group();
                    String version = annotation.version();
                    registry.registerBeanDefinition(cl.getName() + "-" + group + "-" + version,
                            BeanDefinitionBuilder.genericBeanDefinition(cl).getBeanDefinition()
                    );
                });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
