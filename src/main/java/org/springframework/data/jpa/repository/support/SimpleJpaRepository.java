/*
 * Copyright 2008-2013 the original author or authors.
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
package org.springframework.data.jpa.repository.support;

import static org.springframework.data.jpa.repository.query.QueryUtils.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.hybris.repository.core.HybrisEntityMetadata;
import org.springframework.data.hybris.repository.HybrisRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Default implementation of the {@link org.springframework.data.repository.CrudRepository} interface. This will offer
 * you a more sophisticated interface than the plain {@link EntityManager} .
 * 
 * @author Oliver Gierke
 * @author Eberhard Wolff
 * @author Thomas Darimont
 * @param <T> the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 */
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T extends ItemModel, ID extends Serializable> implements HybrisRepository<T, ID> { //, JpaSpecificationExecutor<T> {

    private HybrisEntityMetadata<T> metadata;
    private final FlexibleSearchService flexibleSearchService;
    private final ModelService modelService;
	//private final PersistenceProvider provider;

	//private LockMetadataProvider lockMetadataProvider;

	/**
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
	 * 
	// * @param entityInformation must not be {@literal null}.
	 * @param flexibleSearchService must not be {@literal null}.
	 */
	public SimpleJpaRepository(FlexibleSearchService flexibleSearchService, ModelService modelService, HybrisEntityMetadata<T> metadata) {

		Assert.notNull(metadata);
        Assert.notNull(flexibleSearchService);
        Assert.notNull(modelService);

		this.metadata = metadata;
		this.flexibleSearchService = flexibleSearchService;
        this.modelService = modelService;
		//this.provider = PersistenceProvider.fromEntityManager(entityManager);
	}

	/**
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given domain type.
	 * 
	 * @param domainClass must not be {@literal null}.
	 * @param flexibleSearchService must not be {@literal null}.
	 */
	public SimpleJpaRepository(Class<T> domainClass, FlexibleSearchService flexibleSearchService, ModelService modelService) {
        this(flexibleSearchService, modelService, new HybrisEntityMetadata<T>(domainClass));
	}

//	/**
//	 * Configures a custom {@link LockMetadataProvider} to be used to detect {@link LockModeType}s to be applied to
//	 * queries.
//	 *
//	 * @param lockMetadataProvider
//	 */
//	public void setLockMetadataProvider(LockMetadataProvider lockMetadataProvider) {
//		this.lockMetadataProvider = lockMetadataProvider;
//	}

	protected Class<T> getDomainClass() {
		return metadata.getJavaType();
	}

	private String getDeleteAllQueryString() {
		return getQueryString(DELETE_ALL_QUERY_STRING, metadata.getTypecode());
	}

//	private String getCountQueryString() {
//
//		String countQuery = String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
//		return getQueryString(countQuery, entityInformation.getEntityName());
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
	@Transactional
	public void delete(ID id) {

		Assert.notNull(id, "The given id must not be null!");

		T entity = findOne(id);

		if (entity == null) {
			throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!",
                    metadata.getJavaType(), id), 1);
		}

		delete(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
	 */
	@Transactional
	public void delete(T entity) {

		Assert.notNull(entity, "The entity must not be null!");
		//em.remove(em.contains(entity) ? entity : em.merge(entity));
        modelService.remove(entity);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Transactional
	public void delete(Iterable<? extends T> entities) {

		Assert.notNull(entities, "The given Iterable of entities not be null!");

		for (T entity : entities) {
			delete(entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.Repository#deleteAll()
	 */
	@Transactional
	public void deleteAll() {

		for (T element : findAll()) {
			delete(element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
	 */
	public T findOne(ID id) {

		Assert.notNull(id, "The given id must not be null!");

		//LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
		//Class<T> domainType = getDomainClass();

        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {PK} from {"+ metadata.getTypecode()  +"} where {pk} = ?pk");
        flexibleSearchQuery.setResultClassList(Arrays.asList(metadata.getJavaType()));
        flexibleSearchQuery.addQueryParameter("pk", id);
        T search = flexibleSearchService.searchUnique(flexibleSearchQuery);

        return search;
       // return modelService.get(id);
		//return type == null ? em.find(domainType, id) : em.find(domainType, id, type);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
	 */
	public boolean exists(ID id) {

		Assert.notNull(id, "The given id must not be null!");
        //TODO
//		if (entityInformation.getIdAttribute() != null) {
//
//			String placeholder = provider.getCountQueryPlaceholder();
//			String entityName = entityInformation.getEntityName();
//			Iterable<String> idAttributeNames = entityInformation.getIdAttributeNames();
//			String existsQuery = QueryUtils.getExistsQueryString(entityName, placeholder, idAttributeNames);
//
//			TypedQuery<Long> query = em.createQuery(existsQuery, Long.class);
//
//			if (entityInformation.hasCompositeId()) {
//				for (String idAttributeName : idAttributeNames) {
//					query.setParameter(idAttributeName, entityInformation.getCompositeIdAttributeValue(id, idAttributeName));
//				}
//			} else {
//				query.setParameter(idAttributeNames.iterator().next(), id);
//			}
//
//			return query.getSingleResult() == 1L;
//		} else {
			return findOne(id) != null;
		//}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.hybris.repository.HybrisRepository#findAll()
	 */
	public List<T> findAll() {
		return findAll((Sort) null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll(ID[])
	 */
	public List<T> findAll(Iterable<ID> ids) {

		if (ids == null || !ids.iterator().hasNext()) {
			return Collections.emptyList();
		}

        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {PK} from {"+ metadata.getTypecode()  +"} where {pk} in (?ids)");
        flexibleSearchQuery.addQueryParameter("ids", ids);
        flexibleSearchQuery.setResultClassList(Arrays.asList(metadata.getJavaType()));

        SearchResult<T> search = flexibleSearchService.search(flexibleSearchQuery);

      //  ByIdsSpecification specification = new ByIdsSpecification();
	//	TypedQuery<T> query = getQuery(specification, (Sort) null);

		return search.getResult(); //query.setParameter(specification.parameter, ids).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.hybris.repository.HybrisRepository#findAll(org.springframework.data.domain.Sort)
	 */
	public List<T> findAll(Sort sort) {
        //TODO sort
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {PK} from {"+ metadata.getTypecode()  +"}");

        flexibleSearchQuery.setResultClassList(Arrays.asList(metadata.getJavaType()));

        SearchResult<T> search = flexibleSearchService.search(flexibleSearchQuery);


        return search.getResult();

       // return getQuery(null, sort).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.PagingAndSortingRepository#findAll(org.springframework.data.domain.Pageable)
	 */
	public Page<T> findAll(Pageable pageable) {

		if (null == pageable) {
			return new PageImpl<T>(findAll());
		}
        return findAllInternal(pageable);
		//return findAll(null, pageable); //spec
	}

    private Page<T> findAllInternal(Pageable pageable) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select {PK} from {"+ metadata.getTypecode()  +"}");

        flexibleSearchQuery.setResultClassList(Arrays.asList(metadata.getJavaType()));
        flexibleSearchQuery.setNeedTotal(true);

        SearchResult<T> search = flexibleSearchService.search(flexibleSearchQuery);

        PageImpl<T> ts = new PageImpl<T>(search.getResult(), pageable, search.getTotalCount());
        return ts;
    }

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findOne(org.springframework.data.jpa.domain.Specification)
//	 */
//	public T findOne(Specification<T> spec) {
//
//		try {
//			return getQuery(spec, (Sort) null).getSingleResult();
//		} catch (NoResultException e) {
//			return null;
//		}
//	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification)
//	 */
//	public List<T> findAll(Specification<T> spec) {
//		return getQuery(spec, (Sort) null).getResultList();
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Pageable)
	 */
//	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
//
//		TypedQuery<T> query = getQuery(spec, pageable);
//		return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, spec);
//	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.springframework.data.jpa.domain.Specification, org.springframework.data.domain.Sort)
//	 */
//	public List<T> findAll(Specification<T> spec, Sort sort) {
//
//		return getQuery(spec, sort).getResultList();
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	public long count() {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("select count({PK}) from {"+ metadata.getTypecode()  +"}");
        flexibleSearchQuery.setResultClassList(Arrays.asList(Long.class));

        Long count = flexibleSearchService.<Long>searchUnique(flexibleSearchQuery);

		return count; //em.createQuery(getCountQueryString(), Long.class).getSingleResult();
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor#count(org.springframework.data.jpa.domain.Specification)
//	 */
//	public long count(Specification<T> spec) {
//
//		return getCountQuery(spec).getSingleResult();
//	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#save(java.lang.Object)
	 */
	@Transactional
	public <S extends T> S save(S entity) {

        if(modelService.isNew(entity)) {
            modelService.save(entity);

        } else {
            modelService.attach(entity);
        }

        return entity;

//		if (entityInformation.isNew(entity)) {
//			em.persist(entity);
//			return entity;
//		} else {
//			return em.merge(entity);
//		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.hybris.repository.HybrisRepository#save(java.lang.Iterable)
	 */
	@Transactional
	public <S extends T> List<S> save(Iterable<S> entities) {

		List<S> result = new ArrayList<S>();

		if (entities == null) {
			return result;
		}

		for (S entity : entities) {
			result.add(save(entity));
		}

		return result;
	}

//	/**
//	 * Reads the given {@link TypedQuery} into a {@link Page} applying the given {@link Pageable} and
//	 * {@link Specification}.
//	 *
//	 * @param query must not be {@literal null}.
//	 * @param spec can be {@literal null}.
//	 * @param pageable can be {@literal null}.
//	 * @return
//	 */
//	protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {
//
//		query.setFirstResult(pageable.getOffset());
//		query.setMaxResults(pageable.getPageSize());
//
//		Long total = QueryUtils.executeCountQuery(getCountQuery(spec));
//		List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T> emptyList();
//
//		return new PageImpl<T>(content, pageable, total);
//	}

//	/**
//	 * Creates a new {@link TypedQuery} from the given {@link Specification}.
//	 *
//	 * @param spec can be {@literal null}.
//	 * @param pageable can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {
//
//		Sort sort = pageable == null ? null : pageable.getSort();
//		return getQuery(spec, sort);
//	}

//	/**
//	 * Creates a {@link TypedQuery} for the given {@link Specification} and {@link Sort}.
//	 *
//	 * @param spec can be {@literal null}.
//	 * @param sort can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<T> query = builder.createQuery(getDomainClass());
//
//		Root<T> root = applySpecificationToCriteria(spec, query);
//		query.select(root);
//
//		if (sort != null) {
//			query.orderBy(toOrders(sort, root, builder));
//		}
//
//		return applyLockMode(em.createQuery(query));
//	}

//	/**
//	 * Creates a new count query for the given {@link Specification}.
//	 *
//	 * @param spec can be {@literal null}.
//	 * @return
//	 */
//	protected TypedQuery<Long> getCountQuery(Specification<T> spec) {
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		CriteriaQuery<Long> query = builder.createQuery(Long.class);
//
//		Root<T> root = applySpecificationToCriteria(spec, query);
//
//		if (query.isDistinct()) {
//			query.select(builder.countDistinct(root));
//		} else {
//			query.select(builder.count(root));
//		}
//
//		return em.createQuery(query);
//	}

//	/**
//	 * Applies the given {@link Specification} to the given {@link CriteriaQuery}.
//	 *
//	 * @param spec can be {@literal null}.
//	 * @param query must not be {@literal null}.
//	 * @return
//	 */
//	private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {
//
//		Assert.notNull(query);
//		Root<T> root = query.from(getDomainClass());
//
//		if (spec == null) {
//			return root;
//		}
//
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		Predicate predicate = spec.toPredicate(root, query, builder);
//
//		if (predicate != null) {
//			query.where(predicate);
//		}
//
//		return root;
//	}

    //TODO
//	private TypedQuery<T> applyLockMode(TypedQuery<T> query) {
//
//		LockModeType type = lockMetadataProvider == null ? null : lockMetadataProvider.getLockModeType();
//		return type == null ? query : query.setLockMode(type);
//	}

//	/**
//	 * Specification that gives access to the {@link Parameter} instance used to bind the ids for
//	 * {@link SimpleJpaRepository#findAll(Iterable)}. Workaround for OpenJPA not binding collections to in-clauses
//	 * correctly when using by-name binding.
//	 *
//	 * @see https://issues.apache.org/jira/browse/OPENJPA-2018?focusedCommentId=13924055
//	 * @author Oliver Gierke
//	 */
//	@SuppressWarnings("rawtypes")
//	private final class ByIdsSpecification implements Specification<T> {
//
//		ParameterExpression<Iterable> parameter;
//
//		/*
//		 * (non-Javadoc)
//		 * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
//		 */
//		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//			Path<?> path = root.get(entityInformation.getIdAttribute());
//			parameter = cb.parameter(Iterable.class);
//			return path.in(parameter);
//		}
//	}
}
