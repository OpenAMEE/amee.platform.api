package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.domain.IDataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.Note;
import com.amee.platform.science.ReturnValue;
import com.amee.platform.science.ReturnValues;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemCalculationDOMRenderer_3_4_0 implements DataItemCalculationResource.Renderer {

    @Autowired
    protected IDataItemService dataItemService;

    private DataItem dataItem;
    private Element rootElem;
    private Element returnValuesElem;
    private Element notesElem;
    private Element valuesElem;

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
        returnValuesElem = new Element("Amounts");

        // Add the return values
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {
            Element amountElem = new Element("Amount");
            amountElem.setAttribute("type", entry.getValue().getType());
            amountElem.setAttribute("unit", entry.getValue().getUnit());
            amountElem.setAttribute("perUnit", entry.getValue().getPerUnit());
            if (entry.getKey().equals(returnValues.getDefaultType())) {
                amountElem.setAttribute("default", "true");
            }
            amountElem.setText(Double.toString(entry.getValue().getValue()));
            returnValuesElem.addContent(amountElem);
        }
        if (returnValuesElem.getChildren().size() > 0) {
            rootElem.addContent(returnValuesElem);
        }

        notesElem = new Element("Notes");

        // Add the notes
        for (Note note : returnValues.getNotes()) {
            Element noteElem = new Element("Note");
            noteElem.setAttribute("type", note.getType());
            noteElem.setText(note.getValue());
            notesElem.addContent(noteElem);
        }
        if (notesElem.getChildren().size() > 0) {
            rootElem.addContent(notesElem);
        }
    }

    @Override
    public void addValues(Choices values) {
        valuesElem = new Element("Values");

        // Add the supplied values
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();
        for (Choice choice : values.getChoices()) {
            Element valueElem = new Element("Value");
            valueElem.setAttribute("name", choice.getName());
            valueElem.setText(choice.getValue());

            // Add details from the ItemValueDefinition.
            ItemValueDefinition itemValueDefinition = itemValueDefinitions.get(choice.getName());
            if (itemValueDefinition != null) {
                if (itemValueDefinition.hasUnit()) {
                    valueElem.setAttribute("unit", itemValueDefinition.getUnit().toString());
                }
                if (itemValueDefinition.hasPerUnit()) {
                    valueElem.setAttribute("perUnit", itemValueDefinition.getPerUnit().toString());
                }
            }
            valuesElem.addContent(valueElem);
        }
        if (valuesElem.getChildren().size() > 0) {
            rootElem.addContent(valuesElem);
        }
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
