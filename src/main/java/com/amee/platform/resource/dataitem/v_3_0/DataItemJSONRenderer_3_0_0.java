package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.resource.dataitem.v_3_1.DataItemJSONRenderer_3_1_0;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Version 3.0 does not include the 'history' flag of an ItemValue. Otherwise the representation is the same as 3.1.
 */
@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemJSONRenderer_3_0_0 extends DataItemJSONRenderer_3_1_0 {

    public void newValue(BaseItemValue itemValue) {
        JSONObject valueObj = new JSONObject();
        ResponseHelper.put(valueObj, "path", itemValue.getPath());
        ResponseHelper.put(valueObj, "value", itemValue.getValueAsString());
        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
            NumberValue nv = (NumberValue) itemValue;
            if (nv.hasUnit()) {
                ResponseHelper.put(valueObj, "unit", nv.getUnit().toString());
            }
            if (nv.hasPerUnit()) {
                ResponseHelper.put(valueObj, "perUnit", nv.getPerUnit().toString());
                ResponseHelper.put(valueObj, "compoundUnit", nv.getCompoundUnit().toString());
            }
        }
        valuesArr.put(valueObj);
    }
}
