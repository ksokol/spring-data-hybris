package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.PartTreeJpaQuery;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * @author Kamill Sokol
 */
class CreateQueryLookupStrategy extends AbstractQueryLookupStrategy {

    public CreateQueryLookupStrategy(FlexibleSearchService flexibleSearchService, TypeService typeService) {
        super(flexibleSearchService, typeService);
    }

    @Override
    protected RepositoryQuery resolveQuery(JpaQueryMethod method, FlexibleSearchService flexibleSearchService, NamedQueries namedQueries) {

        try {
            return new PartTreeJpaQuery(method, flexibleSearchService);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Could not create query metamodel for method %s!",
                    method.toString()), e);
        }
    }
}
