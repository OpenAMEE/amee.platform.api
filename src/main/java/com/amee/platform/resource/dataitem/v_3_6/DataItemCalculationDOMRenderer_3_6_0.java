package com.amee.platform.resource.dataitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.DataItemService;
import com.amee.domain.data.BaseItemValueStartDateComparator;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.UsableValuePredicate;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.*;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public void addValues(Choices userValues, Date startDate, Date endDate) {
        Element inputElem = new Element("Input");

        // Add the supplied values
        Element valuesElem = new Element("Values");
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();

        // User values
        for (Choice choice : userValues.getChoices()) {
            if (!choice.getName().startsWith("units.") && !choice.getName().startsWith("perUnits.")) {
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

        // TODO: Move this logic into the builder.
        // Data item values
        for (Map.Entry<String, ItemValueDefinition> entry: itemValueDefinitions.entrySet()) {
            String path = entry.getKey();
            ItemValueDefinition itemValueDefinition = entry.getValue();

            // Only display a value if it hasn't been overridden by the user.
            if (itemValueDefinition.isFromData() && !userValues.containsKey(path)) {
                Element valueElem = new Element("Value");
                valueElem.setAttribute("name", path);
                valueElem.setAttribute("source", "amee");

                // Get all ItemValues with this ItemValueDefinition path.
                List<BaseItemValue> itemValues = dataItemService.getAllItemValues(dataItem, path);

                // Time series?
                if (itemValues.size() > 1) {

                    // Add all BaseItemValues with usable values
                    List<BaseItemValue> usableSet = (List<BaseItemValue>) CollectionUtils.select(itemValues, new UsableValuePredicate());

                    if (!usableSet.isEmpty()) {
                        Element seriesElem = new Element("DataSeries");
                        for (BaseItemValue itemValue: filterItemValues(usableSet, startDate, endDate)) {
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

                            // TODO: How do we deal with start dates and end dates in data calculations
//                            values.put(ivd, new InternalValue(usableSet, dataItem.getEffectiveStartDate(), dataItem.getEffectiveEndDate()));
                        }
                        valueElem.addContent(seriesElem);
                    }

                } else if (itemValues.size() == 1) {
                    BaseItemValue itemValue = itemValues.get(0);
                    if (itemValue.isUsableValue()) {

                        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
                            NumberValue nv = (NumberValue) itemValue;
                            if (nv.hasUnit()) {
                                valueElem.setAttribute("unit", nv.getCompoundUnit().toString());
                            }
                        }

                        valueElem.setText(itemValue.getValueAsString());
                    }
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
    
    private List<BaseItemValue> filterItemValues(List<BaseItemValue> values, Date startDate, Date endDate) {
        List<BaseItemValue> filteredValues = new ArrayList<BaseItemValue>();

        // sort in descending order (most recent last, non-historical value first)
        Collections.sort(values, new BaseItemValueStartDateComparator());

        // endDate can be nil, indicating range-of-interest extends to infinite future time
        // in this case, only the final value in the interval is of interest to anyone
        if (endDate == null) {
            filteredValues.add(values.get(values.size() - 1));
            return filteredValues;
        }

        // The earliest value
        BaseItemValue previous = values.get(0);
        StartEndDate latest;
        if (BaseItemValueStartDateComparator.isHistoricValue(previous)) {
            latest = ((ExternalHistoryValue)previous).getStartDate();
        } else {

            // Set the epoch.
            latest = new StartEndDate(new Date(0));
        }

        for (BaseItemValue iv : values) {
            StartEndDate currentStart;
            if (BaseItemValueStartDateComparator.isHistoricValue(iv)) {
                currentStart = ((ExternalHistoryValue)iv).getStartDate();
            } else {
                currentStart = new StartEndDate(new Date(0));
            }

            if (currentStart.before(endDate) && !currentStart.before(startDate)) {
                filteredValues.add(iv);
            } else if (currentStart.before(startDate) && currentStart.after(latest)) {
                latest = currentStart;
                previous = iv;
            }
        }

        // Add the previous point to the start of the list
        // TODO: WTF?
        if (BaseItemValueStartDateComparator.isHistoricValue(previous)) {
//            slog.info("Adding previous point at " + ((ExternalHistoryValue)previous).getStartDate());
        } else {
//            slog.info("Adding previous point at " + new StartEndDate(new Date(0)));
        }
        filteredValues.add(0, previous);

        return filteredValues;
    }
}
