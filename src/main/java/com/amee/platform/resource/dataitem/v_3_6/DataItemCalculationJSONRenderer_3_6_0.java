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

        JSONObject outputObj = new JSONObject();

        // Create an array of amount objects.
        JSONArray amountsArr = new JSONArray();
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {

            // Create an amount object.
            JSONObject amountObj = new JSONObject();

            String type = entry.getKey();
            ReturnValue returnValue = entry.getValue();

            ResponseHelper.put(amountObj, "type", type);

            // If there was a problem in the calculation, returnValue may be null. (PL-11105)
            ResponseHelper.put(amountObj, "unit", returnValue != null ? returnValue.getCompoundUnit() : "");

            // Flag for default type.
            ResponseHelper.put(amountObj, "default", type.equals(returnValues.getDefaultType()));

            if (returnValue == null) {
                ResponseHelper.put(amountObj, "value", JSONObject.NULL);
            } else if (Double.isInfinite(returnValue.getValue())) {
                ResponseHelper.put(amountObj, "value", "Infinity");
            } else if (Double.isNaN(returnValue.getValue())) {
                ResponseHelper.put(amountObj, "value", "NaN");
            } else {
                ResponseHelper.put(amountObj, "value", returnValue.getValue());
            }

            // Add the object to the amounts array
            amountsArr.put(amountObj);
        }

        // Add the amounts to the output object, if there are some.
        if (amountsArr.length() > 0) {
            ResponseHelper.put(outputObj, "amounts", amountsArr);
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

        // Add the notes array to the the output object, if there are some.
        if (notesArr.length() > 0) {
            ResponseHelper.put(outputObj, "notes", notesArr);
        }

        ResponseHelper.put(rootObj, "output", outputObj);
    }

    @Override
    public void addValues(Choices userValues) {
        JSONObject inputObj = new JSONObject();

        // Create an array of value objects.
        JSONArray valuesArr = new JSONArray();
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();
        for (Choice choice : userValues.getChoices()) {
            if (!choice.getName().startsWith("units.") && !choice.getName().startsWith("perUnits.")) {

                // Create a value object.
                JSONObject valueObj = new JSONObject();
                ResponseHelper.put(valueObj, "name", choice.getName());
                ResponseHelper.put(valueObj, "source", "user");
                ResponseHelper.put(valueObj, "value", choice.getValue());

                // Add details from the ItemValueDefinition.
                ItemValueDefinition itemValueDefinition = itemValueDefinitions.get(choice.getName());
                if (itemValueDefinition != null) {
                    if (itemValueDefinition.hasUnit()) {
                        if (userValues.containsKey("units." + choice.getName())) {
                            ResponseHelper.put(valueObj, "unit", userValues.get("units." + choice.getName()).getValue());
                        } else {
                            ResponseHelper.put(valueObj, "unit", itemValueDefinition.getUnit());
                        }
                        if (itemValueDefinition.hasPerUnit()) {
                            if (userValues.containsKey("perUnits." + choice.getName())) {
                                ResponseHelper.put(valueObj, "perUnit", userValues.get("perUnits." + choice.getName()).getValue());
                            } else {
                                ResponseHelper.put(valueObj, "perUnit", itemValueDefinition.getPerUnit());
                            }
                        }
                    }
                }

                // Add the object to the values array
                valuesArr.put(valueObj);
            }
        }

        // Add the values array to the input object, if there are some.
        if (valuesArr.length() > 0) {
            ResponseHelper.put(inputObj, "values", valuesArr);
        }

        ResponseHelper.put(rootObj, "input", inputObj);
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
