package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitem.v_3_6.DataItemCalculationDOMRenderer_3_6_0;
import com.amee.platform.science.Note;
import com.amee.platform.science.ReturnValue;
import com.amee.platform.science.ReturnValues;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemCalculationDOMRenderer_3_4_0 extends DataItemCalculationDOMRenderer_3_6_0 {

    /**
     * ReturnValues < 3.6.0 were not wrapped in an Output element.
     *
     * @param returnValues
     */
    @Override
    public void addReturnValues(ReturnValues returnValues) {
        Element returnValuesElem = new Element("Amounts");

        // Add the return values
        for (Map.Entry<String, ReturnValue> entry : returnValues.getReturnValues().entrySet()) {
            String type = entry.getKey();
            ReturnValue value = entry.getValue();

            Element amountElem = new Element("Amount");
            amountElem.setAttribute("type", type);
            amountElem.setAttribute("unit", value != null ? value.getUnit() : "");
            amountElem.setAttribute("perUnit", value != null ? value.getPerUnit() : "");
            if (type.equals(returnValues.getDefaultType())) {
                amountElem.setAttribute("default", "true");
            }
            amountElem.setText(value != null ? value.getValue() + "" : "");
            returnValuesElem.addContent(amountElem);
        }
        if (returnValuesElem.getChildren().size() > 0) {
            rootElem.addContent(returnValuesElem);
        }

        Element notesElem = new Element("Notes");

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
}
