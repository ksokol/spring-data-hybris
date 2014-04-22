package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import java.lang.reflect.Method;

/**
 * @author Kamill Sokol
 */
abstract class AbstractQueryLookupStrategy implements QueryLookupStrategy {

    private final FlexibleSearchService flexibleSearchService;
    private final TypeService typeService;

    public AbstractQueryLookupStrategy(FlexibleSearchService flexibleSearchService, TypeService typeService) {
        this.flexibleSearchService = flexibleSearchService;
        this.typeService = typeService;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.repository.query.QueryLookupStrategy#
     * resolveQuery(java.lang.reflect.Method,
     * org.springframework.data.repository.core.RepositoryMetadata,
     * org.springframework.data.repository.core.NamedQueries)
     */
    public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, NamedQueries namedQueries) {
        return resolveQuery(new JpaQueryMethod(method, metadata, typeService), flexibleSearchService, namedQueries);
    }

    protected abstract RepositoryQuery resolveQuery(JpaQueryMethod method, FlexibleSearchService flexibleSearchService, NamedQueries namedQueries);
}
