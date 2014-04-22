package org.springframework.data.hybris.repository.core;

import de.hybris.platform.core.model.ItemModel;
import org.springframework.data.repository.core.EntityMetadata;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

/**
 * @author Kamill Sokol
 */
public class HybrisEntityMetadata<T extends ItemModel> implements EntityMetadata<T> {

    private final Class<T> domainType;
    private final String typecode;

    public HybrisEntityMetadata(Class<T> domainType) {
        Assert.notNull(domainType);
        this.domainType = domainType;
        this.typecode = extractTypecode(domainType);
    }

    @Override
    public Class<T> getJavaType() {
        return domainType;
    }

    public String getTypecode() {
        return typecode;
    }

    private String extractTypecode(Class<?> domainType) {
        try {
            Field typecode = domainType.getDeclaredField("_TYPECODE");
            return (String) typecode.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
