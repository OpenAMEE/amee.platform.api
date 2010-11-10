package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionsDOMRenderer_3_1_0 implements ItemValueDefinitionsResource.Renderer {

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

    public void newItemValueDefinition(ItemValueDefinitionResource.Renderer renderer) {
        itemValueDefinitionsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("ItemValueDefinition").detach());
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
