package org.springframework.data.hybris.repository.core;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;

/**
 * @author Kamill Sokol
 */
public class HybrisEntityInformation<T extends ItemModel, ID extends Serializable> extends HybrisEntityMetadata<T> implements EntityInformation<T, ID> {

    private final ModelService modelService;

    public HybrisEntityInformation(Class<T> domainType, ModelService modelService) {
        super(domainType);
        this.modelService = modelService;
    }

    @Override
    public boolean isNew(T entity) {
        return modelService.isNew(entity);
    }

    @Override
    public ID getId(T entity) {
        return (ID) entity.getPk();
    }

    @Override
    public Class<ID> getIdType() {
        return (Class<ID>) PK.class;
    }
}
