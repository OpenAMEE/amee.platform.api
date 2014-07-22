package com.amee.domain.profile.builder.v2;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.base.utils.UidGen;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemService;
import com.amee.domain.auth.User;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.science.ReturnValues;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ProfileItemBuilderTest {

    private ProfileItemBuilder v2Builder;
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

        v2Builder = new ProfileItemBuilder(profileItem, dataItemService, profileItemService);
        ThreadBeanHolder.set(User.class, new User());
    }

    /**
     * Should properly construct a JSON object when values are NaN or Infinity.
     */
    @Test
    public void InfinityAndNan() throws Exception {
        ReturnValues returnValues = new ReturnValues();
        returnValues.putValue("infinity", "kg", null, Double.POSITIVE_INFINITY);
        returnValues.putValue("nan", "kg", null, Double.NaN);
        returnValues.setDefaultType("infinity");
        profileItem.setAmounts(returnValues);

        JSONObject obj = v2Builder.getJSONObject(false);
        assertEquals("Default amount should be Infinity.", "Infinity", obj.getJSONObject("amount").getString("value"));

        JSONObject amountsObj = obj.getJSONObject("amounts");
        JSONArray amountArray = amountsObj.getJSONArray("amount");

        assertEquals("amountArray should have 2 amounts (JSONObjects) in it", 2, amountArray.length());

        assertTrue("amountArray should contain Infinity and Nan. Got: " + amountArray, hasInfinityAndNan(amountArray));
    }

    private boolean hasInfinityAndNan(JSONArray amountArray) throws Exception {
        boolean hasInfinity = false;
        boolean hasNan = false;
        for (int i = 0; i < amountArray.length(); i++) {
            JSONObject amount = amountArray.getJSONObject(i);
            String type = amount.getString("type");
            String value = amount.getString("value");
            if (type.equals("infinity") && value.equals("Infinity")) {
                hasInfinity = true;
            }
            if (type.equals("nan") && value.equals("NaN")) {
                hasNan = true;
            }
        }
        return hasInfinity && hasNan;
    }
}
