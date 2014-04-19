/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hybris.repository.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;

import org.springframework.data.hybris.dao.support.HybrisPersistenceExceptionTranslator;
import org.springframework.data.hybris.repository.support.HybrisRepositoryFactoryBean;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Hybris specific configuration extension parsing custom attributes from the XML namespace and
 * {@link org.springframework.data.hybris.repository.config.EnableHybrisRepositories} annotation. Also, it registers bean definitions for
 * {@link PersistenceExceptionTranslationPostProcessor} to enable exception translation of persistence specific
 * exceptions into Spring's {@link DataAccessException} hierarchy.
 * 
 * @author Kamill Sokol
 */
public class HybrisRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    private static final String HYBRIS_EXCEPTION_TRANSLATOR_BEAN_NAME = "hybrisPersistenceExceptionTranslator";
	private static final String DEFAULT_TRANSACTION_MANAGER_BEAN_NAME = "transactionManager";
    private static final String DEFAULT_FLEXIBLE_SEARCH_SERVICE_BEAN_NAME = "flexibleSearchService";
    private static final String DEFAULT_MODEL_SERVICE_BEAN_NAME = "modelService";
    private static final String DEFAULT_TYPE_SERVICE_BEAN_NAME = "typeService";

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtension#getRepositoryInterface()
	 */
	public String getRepositoryFactoryClassName() {
		return HybrisRepositoryFactoryBean.class.getName();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#getModulePrefix()
	 */
	@Override
	protected String getModulePrefix() {
		return "hybris";
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config14.RepositoryConfigurationExtensionSupport#postProcessTransactionManager(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config14.XmlRepositoryConfigurationSource)
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
		Element element = config.getElement();
		postProcessTransactionManager(builder, element.getAttribute("transaction-manager-ref"));
        postProcessFlexibleSearchService(builder, element.getAttribute("flexible-search-service-ref"));
        postProcessModelService(builder, element.getAttribute("model-service-ref"));
        postProcessTypeService(builder, element.getAttribute("type-service-ref"));
    }

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcessTransactionManager(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource)
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		AnnotationAttributes attributes = config.getAttributes();
		postProcessTransactionManager(builder, attributes.getString("transactionManagerRef"));
        postProcessFlexibleSearchService(builder, attributes.getString("flexibleSearchServiceRef"));
        postProcessModelService(builder, attributes.getString("modelServiceRef"));
        postProcessTypeService(builder, attributes.getString("typeServiceRef"));
    }

    private void postProcessTransactionManager(BeanDefinitionBuilder builder, String transactionManagerRef) {
        transactionManagerRef = StringUtils.hasText(transactionManagerRef) ? transactionManagerRef 	: DEFAULT_TRANSACTION_MANAGER_BEAN_NAME;
        builder.addPropertyValue("transactionManager", transactionManagerRef);
    }

    private void postProcessFlexibleSearchService(BeanDefinitionBuilder builder, String flexibleSearchServiceRef) {
        flexibleSearchServiceRef = StringUtils.hasText(flexibleSearchServiceRef) ? flexibleSearchServiceRef : DEFAULT_FLEXIBLE_SEARCH_SERVICE_BEAN_NAME;
        builder.addPropertyReference("flexibleSearchService", flexibleSearchServiceRef);
    }

    private void postProcessModelService(BeanDefinitionBuilder builder, String modelServiceRef) {
        modelServiceRef = StringUtils.hasText(modelServiceRef) ? modelServiceRef : DEFAULT_MODEL_SERVICE_BEAN_NAME;
        builder.addPropertyReference("modelService", modelServiceRef);
    }

    private void postProcessTypeService(BeanDefinitionBuilder builder, String typeServiceRef) {
        typeServiceRef = StringUtils.hasText(typeServiceRef) ? typeServiceRef : DEFAULT_TYPE_SERVICE_BEAN_NAME;
        builder.addPropertyReference("typeService", typeServiceRef);
    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#registerBeansForRoot(org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.data.repository.config.RepositoryConfigurationSource)
	 */
	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
		super.registerBeansForRoot(registry, configurationSource);

		if (!hasBean(HybrisPersistenceExceptionTranslator.class, registry)
				&& !registry.containsBeanDefinition(HYBRIS_EXCEPTION_TRANSLATOR_BEAN_NAME)) {
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(HybrisPersistenceExceptionTranslator.class);
            registerWithSourceAndGeneratedBeanName(registry, rootBeanDefinition, configurationSource.getSource());
		}
	}
}
