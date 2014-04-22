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
package org.springframework.data.hybris.repository.support;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.hybris.platform.core.model.ItemModel;
import org.junit.Test;
import org.springframework.data.hybris.repository.core.HybrisEntityMetadata;

/**
 * Unit tests for {@link HybrisEntityMetadata}.
 * 
 * @author Kamill Sokol
 */
public class HybrisEntityMetadataUnitTest {

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void rejectsNullDomainType() {
		new HybrisEntityMetadata(null);
	}

	@Test
	public void returnsConfiguredType() {
		HybrisEntityMetadata<ItemModel> metadata = new HybrisEntityMetadata<ItemModel>(ItemModel.class);
		assertThat(metadata.getJavaType(), is(equalTo(ItemModel.class)));
	}

    @Test
    public void returnsConfiguredTypecode() {
        HybrisEntityMetadata<ItemModel> metadata = new HybrisEntityMetadata<ItemModel>(ItemModel.class);
        assertThat(metadata.getTypecode(), is("Item"));
    }

}
