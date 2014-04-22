/*
 * Copyright 2008-2012 the original author or authors.
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
package org.springframework.data.hybris.repository.support;

import java.io.Serializable;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.hybris.repository.core.HybrisEntityInformation;
import org.springframework.data.hybris.repository.query.lookup.HybrisQueryLookupStrategy;
import org.springframework.data.jpa.repository.support.LockModeRepositoryPostProcessor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.util.Assert;

/**
 * Hybris specific generic repository factory.
 * 
 * @author Oliver Gierke
 */
public class HybrisRepositoryFactory extends RepositoryFactorySupport {

    private final FlexibleSearchService flexibleSearchService;
    private final TypeService typeService;
    private final ModelService modelService;
	private final LockModeRepositoryPostProcessor lockModePostProcessor;

    /**
     * Creates a new {@link HybrisRepositoryFactory}.
     *
     * @param flexibleSearchService must not be {@literal null}
     */
	public HybrisRepositoryFactory(FlexibleSearchService flexibleSearchService, TypeService typeService, ModelService modelService) {
        Assert.notNull(flexibleSearchService);
        Assert.notNull(typeService);
        Assert.notNull(modelService);

        this.flexibleSearchService = flexibleSearchService;
        this.typeService = typeService;
        this.modelService = modelService;
		this.lockModePostProcessor = LockModeRepositoryPostProcessor.INSTANCE;

		addRepositoryProxyPostProcessor(lockModePostProcessor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.support.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
	 */
	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {

        //Class<?> repositoryInterface = metadata.getRepositoryInterface();

        Class<?> domainType = metadata.getDomainType();
        SimpleJpaRepository<?, ?> repo = new SimpleJpaRepository(domainType, flexibleSearchService, modelService);

        //TODO
       // repo.setLockMetadataProvider(lockModePostProcessor.getLockMetadataProvider());

        return repo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.support.RepositoryFactorySupport#
	 * getRepositoryBaseClass()
	 */
	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
	    return SimpleJpaRepository.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.support.RepositoryFactorySupport#
	 * getQueryLookupStrategy
	 * (org.springframework.data.repository.query.QueryLookupStrategy.Key)
	 */
	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key) {
		return HybrisQueryLookupStrategy.create(flexibleSearchService, typeService, key);
	}

    @Override
    public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new HybrisEntityInformation(domainClass, modelService);
    }
}
