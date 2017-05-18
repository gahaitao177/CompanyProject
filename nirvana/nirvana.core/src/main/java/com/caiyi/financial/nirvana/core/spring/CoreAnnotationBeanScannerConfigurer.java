package com.caiyi.financial.nirvana.core.spring;

import com.caiyi.financial.nirvana.core.annotation.Bolt;
import com.caiyi.financial.nirvana.core.constant.ApplicationConstant;
import com.caiyi.financial.nirvana.core.util.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wenshiliang on 2016/12/8.
 */
@Component
public class CoreAnnotationBeanScannerConfigurer implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreAnnotationBeanScannerConfigurer.class);
    public static final String BOLT_ANNOTATION_CLASSNAME = "com.caiyi.financial.nirvana.core.annotation.Bolt";
    private ApplicationContext applicationContext;

    private List<String> boltNameList = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
        scanner.setBeanNameGenerator((definition, registry) -> {
            AnnotatedBeanDefinition annotatedDef = (AnnotatedBeanDefinition) definition;
            AnnotationMetadata amd = annotatedDef.getMetadata();
            Set<String> types = amd.getAnnotationTypes();
            String name = null;
            for (String type : types) {
                AnnotationAttributes attributes = AnnotationAttributes.fromMap(amd.getAnnotationAttributes(type,
                        false));
                if (BOLT_ANNOTATION_CLASSNAME.equals(type)) {
                    //取boltId作为name
                    name = (String) attributes.get("boltId");
                    boltNameList.add(name);
                }
                if (StringUtils.isNoneBlank(name)) {
                    return name;
                }
            }
            throw new IllegalStateException("Stereotype annotations name is  '" + name + "'");
        });
        scanner.setResourceLoader(this.applicationContext);
        String basePackage = SystemConfig.get(ApplicationConstant.ANNOTATION_SCAN);
        LOGGER.info("spring scan "+basePackage);
        scanner.scan(basePackage);
    }

    public List<String> getBoltNameList() {
        return boltNameList;
    }


    public final static class Scanner extends ClassPathBeanDefinitionScanner {
        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        public void registerDefaultFilters() {
            this.addIncludeFilter(new AnnotationTypeFilter(Bolt.class));
        }

        public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
                    .hasAnnotation(Bolt.class.getName());
        }


    }

//    public static class CoreBeanNameGenerator extends AnnotationBeanNameGenerator {
//
//
//
//        @Override
//        protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
//            AnnotationMetadata amd = annotatedDef.getMetadata();
//            Set<String> types = amd.getAnnotationTypes();
//            for (String type : types) {
//                String name = null;
//                AnnotationAttributes attributes = AnnotationAttributes.fromMap(amd.getAnnotationAttributes(type,
//                        false));
//                if(BOLT_ANNOTATION_CLASSNAME.equals(type)){
//                    //取boltId作为name
//                    name = (String) attributes.get("boltId");
//                }
//                if (StringUtils.isNoneBlank(name)) {
//                    return name;
//                }
//            }
//            return super.determineBeanNameFromAnnotation(annotatedDef);
//        }
//    }

}
