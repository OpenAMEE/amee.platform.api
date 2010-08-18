package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.domain.Since;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionsDOMRenderer implements ReturnValueDefinitionsRenderer {

    private Element rootElem;
    private Element returnValueDefinitionsElem;

    public void start() {
        rootElem = new Element("Representation");
        returnValueDefinitionsElem = new Element("ReturnValueDefinitions");
        rootElem.addContent(returnValueDefinitionsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newReturnValueDefinition(ReturnValueDefinitionRenderer returnValueDefinitionRenderer) {
        returnValueDefinitionsElem.addContent(
                ((Document) returnValueDefinitionRenderer.getObject()).getRootElement().getChild("ReturnValueDefinition").detach());
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
