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
package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

/**
 * Query lookup strategy to execute finders.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public final class HybrisQueryLookupStrategy {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private HybrisQueryLookupStrategy() {}

	/**
	 * Creates a {@link QueryLookupStrategy} for the given {@link Key}.
	 * 
	 * @param flexibleSearchService
	 * @param key
	 * @return
	 */
	public static QueryLookupStrategy create(FlexibleSearchService flexibleSearchService, TypeService typeService, Key key) {
		if (key == null) {
	        return new CreateIfNotFoundQueryLookupStrategy(flexibleSearchService, typeService);
		}

		switch (key) {
			case CREATE:
				return new CreateQueryLookupStrategy(flexibleSearchService, typeService);
			case USE_DECLARED_QUERY:
				return new DeclaredQueryLookupStrategy(flexibleSearchService, typeService);
			case CREATE_IF_NOT_FOUND:
				return new CreateIfNotFoundQueryLookupStrategy(flexibleSearchService, typeService);
			default:
				throw new IllegalArgumentException(String.format("Unsupported query lookup strategy %s!", key));
		}
	}
}
