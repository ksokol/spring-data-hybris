package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * @author Kamill Sokol
 */
public class DeclaredQueryLookupStrategy extends AbstractQueryLookupStrategy {


    public DeclaredQueryLookupStrategy(FlexibleSearchService flexibleSearchService, TypeService typeService) {
        super(flexibleSearchService, typeService);
    }

    @Override
    protected RepositoryQuery resolveQuery(JpaQueryMethod method, FlexibleSearchService flexibleSearchService, NamedQueries namedQueries) {

        RepositoryQuery query = JpaQueryFactory.INSTANCE.fromQueryAnnotation(method, flexibleSearchService);

        if (null != query) {
            return query;
        }

        String name = method.getNamedQueryName();
        if (namedQueries.hasQuery(name)) {
            return JpaQueryFactory.INSTANCE.fromMethodWithQueryString(method, flexibleSearchService, namedQueries.getQuery(name));
        }

//			query = NamedQuery.lookupFrom(method, flexibleSearchService);
//
//			if (null != query) {
//				return query;
//			}

        throw new IllegalStateException(String.format(
                "Did neither find a NamedQuery nor an annotated query for method %s!", method));
    }
}
