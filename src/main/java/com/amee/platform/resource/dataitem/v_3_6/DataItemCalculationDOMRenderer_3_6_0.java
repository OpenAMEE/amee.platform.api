package com.amee.platform.resource.dataitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.DataItemService;
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
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemCalculationDOMRenderer_3_6_0 implements DataItemCalculationResource.Renderer {

    @Autowired
    protected DataItemService dataItemService;

    protected DataItem dataItem;
    protected Element rootElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void addDataItem(DataItem dataItem) {
        this.dataItem = dataItem;
    }

    @Override
    public void addReturnValues(ReturnValues returnValues) {
        Element outputElem = new Element("Output");

        // Add the return values
        Element amountsElem = new Element("Amounts");
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {
            String type = entry.getKey();
            ReturnValue value = entry.getValue();

            Element amountElem = new Element("Amount");
            amountElem.setAttribute("type", type);

            // If there was a problem in the calculation, returnValue may be null. (PL-11105)
            amountElem.setAttribute("unit", value != null ? value.getCompoundUnit() : "");
            if (type.equals(returnValues.getDefaultType())) {
                amountElem.setAttribute("default", "true");
            }
            amountElem.setText(value != null ? value.getValue() + "" : "");
            amountsElem.addContent(amountElem);
        }
        if (amountsElem.getChildren().size() > 0) {
            outputElem.addContent(amountsElem);
        }

        // Add the notes
        Element notesElem = new Element("Notes");
        for (Note note : returnValues.getNotes()) {
            Element noteElem = new Element("Note");
            noteElem.setAttribute("type", note.getType());
            noteElem.setText(note.getValue());
            notesElem.addContent(noteElem);
        }
        if (notesElem.getChildren().size() > 0) {
            outputElem.addContent(notesElem);
        }
        rootElem.addContent(outputElem);
    }

    @Override
    public void addValues(Choices userValues, Map<String, List<BaseItemValue>> dataItemValues) {
        Element inputElem = new Element("Input");

        // Add the supplied values
        Element valuesElem = new Element("Values");
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();

        // User values
        for (Choice choice : userValues.getChoices()) {
            if (!choice.getName().startsWith("units.") && !choice.getName().startsWith("perUnits.") &&
                !choice.getName().startsWith("returnUnits.") && !choice.getName().startsWith("returnPerUnits")) {
                Element valueElem = new Element("Value");
                valueElem.setAttribute("name", choice.getName());
                valueElem.setAttribute("source", "user");
                valueElem.setText(choice.getValue());

                // Add details from the ItemValueDefinition.
                ItemValueDefinition itemValueDefinition = itemValueDefinitions.get(choice.getName());
                if (itemValueDefinition != null) {
                    if (itemValueDefinition.hasUnit()) {
                        if (userValues.containsKey("units." + choice.getName())) {
                            valueElem.setAttribute("unit", userValues.get("units." + choice.getName()).getValue());
                        } else {
                            valueElem.setAttribute("unit", itemValueDefinition.getUnit().toString());
                        }
                    }
                    if (itemValueDefinition.hasPerUnit()) {
                        if (userValues.containsKey("perUnits." + choice.getName())) {
                            valueElem.setAttribute("perUnit", userValues.get("perUnits." + choice.getName()).getValue());
                        } else {
                            valueElem.setAttribute("perUnit", itemValueDefinition.getPerUnit().toString());
                        }
                    }
                }
                valuesElem.addContent(valueElem);
            }
        }

        // Data item values
        for (Map.Entry<String, List<BaseItemValue>> entry: dataItemValues.entrySet()) {
            String path = entry.getKey();
            List<BaseItemValue> itemValues = entry.getValue();

            // Only display a value if it hasn't been overridden by the user.
            if (!userValues.containsKey(path)) {
                Element valueElem = new Element("Value");
                valueElem.setAttribute("name", path);
                valueElem.setAttribute("source", "amee");

                // Time series?
                if (itemValues.size() > 1) {
                    Element seriesElem = new Element("DataSeries");
                    for (BaseItemValue itemValue: itemValues) {
                        Element dataPointElem = new Element("DataPoint");
                        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
                            NumberValue nv = (NumberValue) itemValue;
                            if (nv.hasUnit()) {
                                dataPointElem.setAttribute("unit", nv.getCompoundUnit().toString());
                            }
                        }
                        if (ExternalHistoryValue.class.isAssignableFrom(itemValue.getClass())) {
                            dataPointElem.setAttribute("startDate", ((ExternalHistoryValue) itemValue).getStartDate().toString());
                        } else {
                            dataPointElem.setAttribute("startDate", DATE_FORMAT.print(new DateTime(0)));
                        }
                        dataPointElem.setText(itemValue.getValueAsString());
                        seriesElem.addContent(dataPointElem);
                    }
                    valueElem.addContent(seriesElem);

                } else if (itemValues.size() == 1) {
                    BaseItemValue itemValue = itemValues.get(0);
                    if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
                        NumberValue nv = (NumberValue) itemValue;
                        if (nv.hasUnit()) {
                            valueElem.setAttribute("unit", nv.getCompoundUnit().toString());
                        }
                    }

                    valueElem.setText(itemValue.getValueAsString());
                }
                valuesElem.addContent(valueElem);
            }
        }

        // Only add the element if we have values.
        if (valuesElem.getChildren().size() > 0) {
            inputElem.addContent(valuesElem);
        }
        rootElem.addContent(inputElem);
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
