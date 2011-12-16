package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.dataitem.v_3_6.DataItemCalculationJSONRenderer_3_6_0;
import com.amee.platform.science.Note;
import com.amee.platform.science.ReturnValue;
import com.amee.platform.science.ReturnValues;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemCalculationJSONRenderer_3_4_0 extends DataItemCalculationJSONRenderer_3_6_0 {

    /**
     * v3.4.0 did not handle non-finite numbers in json.
     * 
     * @param returnValues
     */
    @Override
    public void addReturnValues(ReturnValues returnValues) {

        // Create an array of multiple amount objects.
        JSONArray multipleAmountsArr = new JSONArray();
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {
            // Create a multiple amount object.
            JSONObject multipleAmountObj = new JSONObject();
            ResponseHelper.put(multipleAmountObj, "value", entry.getValue().getValue());
            ResponseHelper.put(multipleAmountObj, "type", entry.getKey());
            ResponseHelper.put(multipleAmountObj, "unit", entry.getValue().getUnit());
            ResponseHelper.put(multipleAmountObj, "perUnit", entry.getValue().getPerUnit());
            // Flag for default type.
            ResponseHelper.put(multipleAmountObj, "default", entry.getKey().equals(returnValues.getDefaultType()));
            // Add the object to the amounts array
            multipleAmountsArr.put(multipleAmountObj);
        }

        // Add the multiple amounts to the result object, if there are some.
        if (multipleAmountsArr.length() > 0) {
            ResponseHelper.put(rootObj, "amounts", multipleAmountsArr);
        }

        // Create an array of note objects.
        JSONArray notesArr = new JSONArray();
        for (Note note : returnValues.getNotes()) {
            // Create the note object.
            JSONObject noteObj = new JSONObject();
            ResponseHelper.put(noteObj, "type", note.getType());
            ResponseHelper.put(noteObj, "value", note.getValue());
            // Add the note object to the notes array
            notesArr.put(noteObj);
        }

        // Add the notes array to the the result object, if there are some.
        if (notesArr.length() > 0) {
            ResponseHelper.put(rootObj, "notes", notesArr);
        }
    }
}
