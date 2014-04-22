package org.springframework.data.hybris.repository.query.lookup;

import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.query.RepositoryQuery;

/**
 * @author Kamill Sokol
 */
public class CreateIfNotFoundQueryLookupStrategy extends AbstractQueryLookupStrategy {

    private final DeclaredQueryLookupStrategy strategy;
    private final CreateQueryLookupStrategy createStrategy;

    public CreateIfNotFoundQueryLookupStrategy(FlexibleSearchService flexibleSearchService, TypeService typeService) {
        super(flexibleSearchService, typeService);
        this.strategy = new DeclaredQueryLookupStrategy(flexibleSearchService, typeService);
        this.createStrategy = new CreateQueryLookupStrategy(flexibleSearchService, typeService);
    }

    @Override
    protected RepositoryQuery resolveQuery(JpaQueryMethod method, FlexibleSearchService flexibleSearchService, NamedQueries namedQueries) {

        try {
            return strategy.resolveQuery(method, flexibleSearchService, namedQueries);
        } catch (IllegalStateException e) {
            return createStrategy.resolveQuery(method, flexibleSearchService, namedQueries);
        }
    }
}
