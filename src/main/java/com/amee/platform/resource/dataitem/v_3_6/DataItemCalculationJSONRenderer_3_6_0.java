package com.amee.platform.resource.dataitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.DataItemService;
import com.amee.domain.ValueType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.ExternalHistoryValue;
import com.amee.platform.science.Note;
import com.amee.platform.science.ReturnValue;
import com.amee.platform.science.ReturnValues;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemCalculationJSONRenderer_3_6_0 implements DataItemCalculationResource.Renderer {

    @Autowired
    protected DataItemService dataItemService;

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
    public void addValues(DataItem dataItem, Choices userValues, Map<String, List<BaseItemValue>> dataItemValues) {
        JSONObject inputObj = new JSONObject();

        // Create an array of value objects.
        JSONArray valuesArr = new JSONArray();
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();

        // User values
        for (Choice choice : userValues.getChoices()) {
            if (!choice.getName().startsWith("units.") && !choice.getName().startsWith("perUnits.") &&
                !choice.getName().startsWith("returnUnits.") && !choice.getName().startsWith("returnPerUnits")) {

                // Create a value object.
                JSONObject valueObj = new JSONObject();
                ResponseHelper.put(valueObj, "name", choice.getName());
                ResponseHelper.put(valueObj, "source", "user");

                ValueType type = itemValueDefinitions.get(choice.getName()).getValueDefinition().getValueType();
                String value = choice.getValue();
                if (ValueType.TEXT.equals(type)) {
                    ResponseHelper.put(valueObj, "value", value);
                } else {

                    // Non-text values that are represented here by the empty string should be null in JSON
                    if (value == null || "".equals(value)) {
                        ResponseHelper.put(valueObj, "value", JSONObject.NULL);
                    } else {
                        switch (type) {
                            case BOOLEAN:
                                ResponseHelper.put(valueObj, "value", Boolean.valueOf(value));
                                break;
                            case DOUBLE:
                                ResponseHelper.put(valueObj, "value", Double.valueOf(value));
                                break;
                            case INTEGER:
                                ResponseHelper.put(valueObj, "value", Integer.valueOf(value));
                                break;
                        }
                    }
                }

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

        // Data item values
        for (Map.Entry<String, List<BaseItemValue>> entry: dataItemValues.entrySet()) {
            String path = entry.getKey();
            List<BaseItemValue> itemValues = entry.getValue();

            // Only display a value if it hasn't been overridden by the user.
            if (!userValues.containsKey(path)) {
                JSONObject valueObj = new JSONObject();
                ResponseHelper.put(valueObj, "name", path);
                ResponseHelper.put(valueObj, "source", "amee");

                // Time series?
                if (itemValues.size() > 1) {
                    JSONArray seriesArray = new JSONArray();
                    for (BaseItemValue itemValue: itemValues) {
                        JSONObject dataPointObj = new JSONObject();
                        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
                            NumberValue nv = (NumberValue) itemValue;
                            if (nv.hasUnit()) {
                                ResponseHelper.put(dataPointObj, "unit", nv.getCompoundUnit().toString());
                            }
                        }
                        if (ExternalHistoryValue.class.isAssignableFrom(itemValue.getClass())) {
                            ResponseHelper.put(dataPointObj, "startDate", ((ExternalHistoryValue) itemValue).getStartDate().toString());
                        } else {
                            ResponseHelper.put(dataPointObj, "startDate", DATE_FORMAT.print(new DateTime(0)));
                        }

                        ValueType type = itemValueDefinitions.get(path).getValueDefinition().getValueType();
                        String value = itemValue.getValueAsString();
                        if (ValueType.TEXT.equals(type)) {
                            ResponseHelper.put(dataPointObj, "value", value);
                        } else {

                            // Non-text values that are represented here by the empty string should be null in JSON
                            if (value == null || "".equals(value)) {
                                ResponseHelper.put(dataPointObj, "value", JSONObject.NULL);
                            } else {
                                switch (type) {
                                    case BOOLEAN:
                                        ResponseHelper.put(dataPointObj, "value", Boolean.valueOf(value));
                                        break;
                                    case DOUBLE:
                                        ResponseHelper.put(dataPointObj, "value", Double.valueOf(value));
                                        break;
                                    case INTEGER:
                                        ResponseHelper.put(dataPointObj, "value", Integer.valueOf(value));
                                        break;
                                }
                            }
                        }
                        seriesArray.put(dataPointObj);
                    }
                    ResponseHelper.put(valueObj, "value", seriesArray);
                } else if (itemValues.size() == 1) {
                    BaseItemValue itemValue = itemValues.get(0);
                    if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
                        NumberValue nv = (NumberValue) itemValue;
                        if (nv.hasUnit()) {
                            ResponseHelper.put(valueObj, "unit", nv.getCompoundUnit().toString());
                        }
                    }

                    ValueType type = itemValueDefinitions.get(path).getValueDefinition().getValueType();
                    String value = itemValue.getValueAsString();
                    if (ValueType.TEXT.equals(type)) {
                        ResponseHelper.put(valueObj, "value", value);
                    } else {

                        // Non-text values that are represented here by the empty string should be null in JSON
                        if (value == null || "".equals(value)) {
                            ResponseHelper.put(valueObj, "value", JSONObject.NULL);
                        } else {
                            switch (type) {
                                case BOOLEAN:
                                    ResponseHelper.put(valueObj, "value", Boolean.valueOf(value));
                                    break;
                                case DOUBLE:
                                    ResponseHelper.put(valueObj, "value", Double.valueOf(value));
                                    break;
                                case INTEGER:
                                    ResponseHelper.put(valueObj, "value", Integer.valueOf(value));
                                    break;
                            }
                        }
                    }
                }
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
