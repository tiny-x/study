package com.xy.spring.bean.post.process;

import com.xy.spring.bean.post.process.Bean.Service;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

@Component
public class BeanDefinitionPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        Set<BeanDefinition> definitions = scanner.findCandidateComponents("com.xy.spring.bean.post.process.Bean");

        for (BeanDefinition definition : definitions) {
            if (definition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) definition;
                Map<String, Object> annotationAttributes = annotatedBeanDefinition.getMetadata().getAnnotationAttributes(Service.class.getName());
                AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationAttributes);
                String beanName = attributes.getString("name");
                if (StringUtils.isEmpty(beanName)) {
                    beanName = BeanDefinitionReaderUtils.generateBeanName(definition, registry);
                    registry.registerBeanDefinition(beanName, definition);
                } else {
                    registry.registerBeanDefinition(beanName, definition);
                }
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
