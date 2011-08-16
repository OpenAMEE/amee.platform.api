package com.amee.domain.profile.builder.v1;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.base.utils.UidGen;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemService;
import com.amee.domain.auth.User;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.science.ReturnValues;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProfileItemBuilderTest {

    private ProfileItemBuilder v1Builder;
    private ProfileItem profileItem;
    private DataItem dataItem;

    @Mock private DataItemService dataItemService;
    @Mock private ProfileItemService profileItemService;

    @Before
    public void setUp() throws Exception {
        profileItem = new ProfileItem();
        profileItem.setCreated(new Date());
        profileItem.setModified(new Date());
        profileItem.setName("Test");
        profileItem.setUid(UidGen.INSTANCE_12.getUid());

        dataItem = new DataItem();
        dataItem.setUid(UidGen.INSTANCE_12.getUid());
        profileItem.setDataItem(dataItem);

        v1Builder = new ProfileItemBuilder(profileItem, dataItemService, profileItemService);
        ThreadBeanHolder.set(User.class, new User());
    }

    /**
     * Should properly construct a JSON object when values are Infinity.
     */
    @Test
    public void infinity() throws Exception {
        ReturnValues returnValues = new ReturnValues();
        returnValues.putValue("infinity", "kg", null, Double.POSITIVE_INFINITY);
        returnValues.setDefaultType("infinity");
        profileItem.setAmounts(returnValues);

        JSONObject obj = v1Builder.getJSONObject(false);
        assertEquals("Amount should be Infinity.", "Infinity", obj.getString("amountPerMonth"));
    }

    /**
     * Should properly construct a JSON object when values are NaN.
     */
    @Test
    public void nan() throws Exception {
        ReturnValues returnValues = new ReturnValues();
        returnValues.putValue("nan", "kg", null, Double.NaN);
        returnValues.setDefaultType("nan");
        profileItem.setAmounts(returnValues);

        JSONObject obj = v1Builder.getJSONObject(false);
        assertEquals("Default amount should be NaN.", "NaN", obj.getString("amountPerMonth"));
    }
}
