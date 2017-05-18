package com.caiyi.financial.nirvana.core.spring.demo2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BeanScannerConfigurer implements BeanFactoryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//     ClassPathBeanDefinitionScanner scanner =  new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) beanFactory);
//     System.out.println(clsScanner);
//        String[] basePackagesArray = componentScan.getAliasedStringArray("basePackages", ComponentScan.class, declaringClass);

        Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
        scanner.setBeanNameGenerator(new CustomizeComponentBeanNameGenerator());
        scanner.setResourceLoader(this.applicationContext);
        scanner.scan("**.spring.demo2");
    }

    public final static class Scanner extends ClassPathBeanDefinitionScanner {
        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);

        }

        public void registerDefaultFilters() {
            this.addIncludeFilter(new AnnotationTypeFilter(CustomizeComponent.class));
        }

        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
//            for (BeanDefinitionHolder holder : beanDefinitions) {
//                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
//                definition.getPropertyValues().add("innerClassName", definition.getBeanClassName());
//                definition.setBeanClass(FactoryBeanTest.class);
//                definition.setParentName("t1");
//                definition.setBeanClassName("t1");
//            }
            return beanDefinitions;
        }

        public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
                    .hasAnnotation(CustomizeComponent.class.getName());
        }

//        public void aaa(BeanDefinition candidate){
//
//            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
//            candidate.setScope(scopeMetadata.getScopeName());
//            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
//            if (candidate instanceof AbstractBeanDefinition) {
//                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
//            }
//            if (candidate instanceof AnnotatedBeanDefinition) {
//                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
//            }
//            if (checkCandidate(beanName, candidate)) {
//                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
//                definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
//                beanDefinitions.add(definitionHolder);
//                registerBeanDefinition(definitionHolder, this.registry);
//            }
//        }

    }

    public static class CustomizeComponentBeanNameGenerator extends AnnotationBeanNameGenerator {

        @Override
        protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
//            annotatedDef.get
            AnnotationMetadata amd = annotatedDef.getMetadata();
            Set<String> types = amd.getAnnotationTypes();
//            String beanName = null;
            for (String type : types) {
                AnnotationAttributes attributes = AnnotationAttributes.fromMap(amd.getAnnotationAttributes(type, false));
                Set<String> set = amd.getMetaAnnotationTypes(type);
                System.out.println(set);
                String name = (String) attributes.get("name");
                if(StringUtils.isNoneBlank(name)){
                    return name;
                }
//
//   AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(amd, type);
            }
            return super.determineBeanNameFromAnnotation(annotatedDef);
        }
    }
}

