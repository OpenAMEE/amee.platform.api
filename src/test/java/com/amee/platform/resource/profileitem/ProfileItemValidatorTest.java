package com.amee.platform.resource.profileitem;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.DataItemService;
import com.amee.domain.Metadata;
import com.amee.domain.MetadataService;
import com.amee.domain.ProfileItemService;
import com.amee.domain.ValueDefinition;
import com.amee.domain.ValueType;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.resource.profileitem.v_3_6.ProfileItemValidator_3_6_0;

@RunWith(MockitoJUnitRunner.class)
public class ProfileItemValidatorTest {

    @Mock
    private ProfileItemService mockProfileItemService;

    @Mock
    private ItemDefinition mockItemDefinition;
    
    @Mock MetadataService mockMetadataService;

    @Before
    public void setUp() {
        Set<ItemValueDefinition> itemValueDefinitions = new HashSet<ItemValueDefinition>();
        itemValueDefinitions.add(getItemValueDefinition("ivd1", "path1", new ValueDefinition("vd1", ValueType.DOUBLE)));
        itemValueDefinitions.add(getItemValueDefinition("ivd2", "path2", new ValueDefinition("vd2", ValueType.TEXT)));
        when(mockItemDefinition.getActiveItemValueDefinitions())
            .thenReturn(itemValueDefinitions);
        
        when(mockItemDefinition.getProfileItemValuesBean())
            .thenCallRealMethod();
        when(mockItemDefinition.getProfileItemUnitsBean())
            .thenCallRealMethod();
        
        ThreadBeanHolder.clear();
        ThreadBeanHolder.set(MetadataService.class, mockMetadataService);
    }
    
    @Test
    public void testValid() {
        ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
        validator.setProfileItemService(mockProfileItemService);
        ProfileItem good = new ProfileItem();
        good.setItemDefinition(mockItemDefinition);
        when(mockProfileItemService.isUnique(good))
            .thenReturn(true);
        validator.setObject(good);
        validator.initialise();
        BindException errorsGood = new BindException(good, "good");

        good.setName(RandomStringUtils.random(10));
        good.setStartDate(new DateTime(2010, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toDate());
        good.setDuration("P10Y3M2D");
        good.setNote(RandomStringUtils.random(30));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }
    
    @Test
    public void testNameGreaterThanMax() {
        ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
        validator.setProfileItemService(mockProfileItemService);
        ProfileItem bad = new ProfileItem();
        bad.setItemDefinition(mockItemDefinition);
        when(mockProfileItemService.isUnique(bad))
            .thenReturn(true);
        validator.setObject(bad);
        validator.initialise();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(RandomStringUtils.random(ProfileItem.NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
	
	@Test
	public void testEndDateBeforeStartDate(){
		ProfileItem bad = new ProfileItem();
		bad.setStartDate(new Date());
		bad.setEndDate(new Date(bad.getStartDate().getTime() - 1));
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());	
	}

	@Test
	public void testEmptyName(){
		ProfileItem good = new ProfileItem();
		good.setName("");
		good.setItemDefinition(mockItemDefinition);
		
		BindException errorsGood = new BindException(good, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(good);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(good)).thenReturn(true);
		
		validator.validate(good, errorsGood);
		
		assertFalse("Object should not fail validation", errorsGood.hasErrors());
	}	 
	
	@Test
	public void testStartDateTooEarly(){
		ProfileItem bad = new ProfileItem();
		bad.setStartDate(new Date(DataItemService.EPOCH.getTime() - 1));
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());
	}
	
	@Test
	public void testStartDateTooLate(){
		ProfileItem bad = new ProfileItem();
		bad.setStartDate(new Date(DataItemService.Y2038.getTime() + 1));
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());
	}
	
	@Test
	public void testEndDateTooLate(){
		ProfileItem bad = new ProfileItem();
		bad.setEndDate(new Date(DataItemService.Y2038.getTime() + 1));
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());
	}

    /**
     * Tests duration not in correct ISO 8601 format. Should fail validation.
     */
    @Test
    public void testDurationBadFormat() {
        ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
        validator.setProfileItemService(mockProfileItemService);
        ProfileItem bad = new ProfileItem();
        bad.setItemDefinition(mockItemDefinition);
        when(mockProfileItemService.isUnique(bad))
            .thenReturn(true);
        validator.setObject(bad);
        validator.initialise();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setDuration("NOT A VALID ISO 8601 DURATION");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
	
	@Test
	public void testDurationTooLong(){
		ProfileItem bad = new ProfileItem();
		bad.setStartDate(new Date());
		bad.setDuration("P1000Y");
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());
	}

    /**
     * Tests start date after end date. Should fail validation.
     */
    @Test
    public void testBadDates() {
        ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
        validator.setProfileItemService(mockProfileItemService);
        ProfileItem bad = new ProfileItem();
        bad.setItemDefinition(mockItemDefinition);
        when(mockProfileItemService.isUnique(bad))
            .thenReturn(true);
        validator.setObject(bad);
        validator.initialise();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setStartDate(new DateTime(2010, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC).toDate());
        bad.setEndDate(new DateTime(2008, 1, 1, 1, 0, 0, 0, DateTimeZone.UTC).toDate());

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
	@Test
	public void testNoteGreaterThanMax(){
		ProfileItem bad = new ProfileItem();
		bad.setNote(RandomStringUtils.random(Metadata.VALUE_MAX_SIZE + 1));
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should fail validation", errorsBad.hasErrors());
	}
	
	@Test
	public void testEmptyNote(){
		ProfileItem bad = new ProfileItem();
		bad.setNote("");
		bad.setItemDefinition(mockItemDefinition);
		
		BindException errorsBad = new BindException(bad, "bad");
		
		ProfileItemValidator_3_6_0 validator = new ProfileItemValidator_3_6_0();
		validator.setProfileItemService(mockProfileItemService);
		validator.setObject(bad);
		validator.initialise();
		
		when(mockProfileItemService.isUnique(bad)).thenReturn(true);
		
		validator.validate(bad, errorsBad);
		
		assertTrue("Object should not fail validation", !errorsBad.hasErrors());
	}	

    private ItemValueDefinition getItemValueDefinition(String name, String path, ValueDefinition valueDefinition) {
        ItemValueDefinition ivd = new ItemValueDefinition();
        ivd.setName(name);
        ivd.setFromProfile(true);
        ivd.setPath(path);
        ivd.setValueDefinition(valueDefinition);
        return ivd;
    }
}
