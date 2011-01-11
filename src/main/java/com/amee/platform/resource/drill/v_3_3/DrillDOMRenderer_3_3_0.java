package com.amee.platform.resource.drill.v_3_3;

import com.amee.base.domain.Since;
import com.amee.domain.sheet.Choice;
import com.amee.platform.resource.drill.DrillResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DrillDOMRenderer_3_3_0 implements DrillResource.Renderer {

    private Element rootElem;
    private Element drillElem;
    private Element selectionsElem;
    private Element choicesElem;
    private Element valuesElem;

    public void start() {
        rootElem = new Element("Representation");
        drillElem = new Element("Drill");
        rootElem.addContent(drillElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void startSelections() {
        selectionsElem = new Element("Selections");
        drillElem.addContent(selectionsElem);
    }

    public void newSelection(Choice selection) {
        Element elem = new Element("Selection");
        selectionsElem.addContent(elem);
        if (!selection.getValue().equals(selection.getName())) {
            elem.addContent(new Element("Name").setText(selection.getName()));
        }
        elem.addContent(new Element("Value").setText(selection.getValue()));
    }

    public void startChoices(String name) {
        choicesElem = new Element("Choices");
        drillElem.addContent(choicesElem);
        choicesElem.addContent(new Element("Name").setText(name));
        valuesElem = new Element("Values");
        choicesElem.addContent(valuesElem);
    }

    public void newChoice(Choice choice) {
        valuesElem.addContent(new Element("Value").setText(choice.getValue()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
