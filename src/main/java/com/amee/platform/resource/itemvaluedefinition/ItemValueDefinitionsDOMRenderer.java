package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.domain.Since;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionsDOMRenderer implements ItemValueDefinitionsRenderer {

    private Element rootElem;
    private Element itemValueDefinitionsElem;

    public void start() {
        rootElem = new Element("Representation");
        itemValueDefinitionsElem = new Element("ItemValueDefinitions");
        rootElem.addContent(itemValueDefinitionsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newItemValueDefinition(ItemValueDefinitionRenderer itemValueDefinitionRenderer) {
        itemValueDefinitionsElem.addContent(((Document) itemValueDefinitionRenderer.getObject()).getRootElement().getChild("ItemValueDefinition").detach());
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
