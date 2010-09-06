package com.amee.domain.profile;

import com.amee.domain.AMEEStatus;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProfileItemTest {

    private DataCategory mockDataCategory;
    private ItemDefinition mockItemDefinition;
    private DataItem dataItem;
    private Profile mockProfile;
    private ProfileItem profileItem;

    @Before
    public void setUp() throws Exception {
        mockDataCategory = mock(DataCategory.class);
        mockItemDefinition = mock(ItemDefinition.class);
        dataItem = new DataItem(mockDataCategory, mockItemDefinition);
        mockProfile = mock(Profile.class);
        profileItem = new ProfileItem(mockProfile, dataItem);
    }

    @Test
    public void noneTrashed() {
        // A ProfileItem should be considered trashed if:
        // itself is trashed or its DataItem is trashed or its Profile is trashed.
        dataItem.setStatus(AMEEStatus.ACTIVE);
        when(mockProfile.isTrash()).thenReturn(false);
        assertFalse("ProfileItem should not be trashed", profileItem.isTrash());
        verify(mockProfile).isTrash();
    }

    @Test
    public void itselfTrashed() {
        profileItem.setStatus(AMEEStatus.TRASH);
        assertTrue("ProfileItem should be trashed", profileItem.isTrash());
    }

    @Test
    public void dataItemTrashed() {
        dataItem.setStatus(AMEEStatus.TRASH);
        assertTrue("ProfileItem should be trashed", profileItem.isTrash());
    }

    @Test
    public void profileTrashed() {
        dataItem.setStatus(AMEEStatus.ACTIVE);
        when(mockProfile.isTrash()).thenReturn(true);
        assertTrue("ProfileItem should be trashed", profileItem.isTrash());
        verify(mockProfile).isTrash();
    }
}
