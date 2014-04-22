/*
 * Copyright 2013 the original author or authors.
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
package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.hybris.repository.Query;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.SimpleJpaQuery;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * Factory to create the appropriate {@link RepositoryQuery} for a {@link org.springframework.data.jpa.repository.query.JpaQueryMethod}.
 * 
 * @author Thomas Darimont
 */
enum JpaQueryFactory {

	INSTANCE;

	private static final Logger log = LoggerFactory.getLogger(JpaQueryFactory.class);

	/**
	 * Creates a {@link RepositoryQuery} from the given {@link QueryMethod} that is potentially annotated with
	 * {@link Query}.
	 * 
	 * @param queryMethod must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @return the {@link RepositoryQuery} derived from the annotation or {@code null} if no annotation found.
	 */
	AbstractJpaQuery fromQueryAnnotation(JpaQueryMethod queryMethod, FlexibleSearchService em) {
		log.debug("Looking up query for method {}", queryMethod.getName());
		return fromMethodWithQueryString(queryMethod, em, queryMethod.getAnnotatedQuery());
	}

	/**
	 * Creates a {@link RepositoryQuery} from the given {@link String} query.
	 * 
	 * @param method must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 * @param queryString must not be {@literal null} or empty.
	 * @return
	 */
	AbstractJpaQuery fromMethodWithQueryString(JpaQueryMethod method, FlexibleSearchService em, String queryString) {
		if (queryString == null) {
			return null;
		}

        return new SimpleJpaQuery(method, em, queryString);

		//return method.isNativeQuery() ? new NativeJpaQuery(method, em, queryString) : //
	//			new SimpleJpaQuery(method, em, queryString);
	}
}
