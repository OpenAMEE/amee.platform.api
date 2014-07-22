package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetadataTest {

    @Mock private AMEEEntityReference mockDataCategoryReference;
    @Mock private AMEEEntity mockDataCategory;

    private Metadata metadata;

    @Before
    public void setUp() throws Exception {
        when(mockDataCategoryReference.getEntity())
            .thenReturn(mockDataCategory);

        metadata = new Metadata();
        metadata.setEntityReference(mockDataCategoryReference);

    }

    @Test
    public void noneTrashed() throws Exception {

        // A Metadata should be considered trashed if:
        // itself is trashed or its Entity (DC, IVD, DI, ID) is trashed.

        when(mockDataCategory.isTrash())
            .thenReturn(false);

        assertFalse("Metadata should not be trashed.", metadata.isTrash());
        verify(mockDataCategory).isTrash();
    }

    @Test
    public void entityTrashed() throws Exception {
        when(mockDataCategory.isTrash())
            .thenReturn(true);

        assertTrue("Metadata should be trashed.", metadata.isTrash());
        verify(mockDataCategory).isTrash();
    }
}
