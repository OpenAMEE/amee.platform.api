package com.amee.platform.resource.dataitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.Note;
import com.amee.platform.science.ReturnValue;
import com.amee.platform.science.ReturnValues;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemCalculationJSONRenderer_3_6_0 implements DataItemCalculationResource.Renderer {

    @Autowired
    protected DataItemService dataItemService;

    protected DataItem dataItem;
    protected JSONObject rootObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void addDataItem(DataItem dataItem) {
        this.dataItem = dataItem;
    }

    @Override
    public void addReturnValues(ReturnValues returnValues) {

        // Create an array of multiple amount objects.
        JSONArray multipleAmountsArr = new JSONArray();
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {

            // Create a multiple amount object.
            JSONObject multipleAmountObj = new JSONObject();

            Double returnValue = entry.getValue().getValue();
            if (returnValue == null) {
                ResponseHelper.put(multipleAmountObj, "value", JSONObject.NULL);
            } else if (returnValue.isInfinite()) {
                ResponseHelper.put(multipleAmountObj, "value", "Infinity");
            } else if (returnValue.isNaN()) {
                ResponseHelper.put(multipleAmountObj, "value", "NaN");
            } else {
                ResponseHelper.put(multipleAmountObj, "value", returnValue);
            }
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

    @Override
    public void addValues(Choices values) {

        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();

        // Create an array of value objects.
        JSONArray valuesArr = new JSONArray();
        for (Choice choice : values.getChoices()) {
            // Create a value object.
            JSONObject valueObj = new JSONObject();
            ResponseHelper.put(valueObj, "name", choice.getName());
            ResponseHelper.put(valueObj, "value", choice.getValue());
            // Add details from the ItemValueDefinition.
            ItemValueDefinition itemValueDefinition = itemValueDefinitions.get(choice.getName());
            if (itemValueDefinition != null) {
                if (itemValueDefinition.hasUnit()) {
                    ResponseHelper.put(valueObj, "unit", itemValueDefinition.getUnit());
                    if (itemValueDefinition.hasPerUnit()) {
                        ResponseHelper.put(valueObj, "perUnit", itemValueDefinition.getPerUnit());
                    }
                }
            }
            // Add the object to the amounts array
            valuesArr.put(valueObj);
        }

        // Add the value objects to the result object, if there are some.
        if (valuesArr.length() > 0) {
            ResponseHelper.put(rootObj, "values", valuesArr);
        }
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
