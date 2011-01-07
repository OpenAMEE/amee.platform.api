package com.amee.platform.resource.itemdefinition.v_3_3;

import com.amee.base.domain.Since;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import com.amee.platform.resource.itemdefinition.ItemDefinitionsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.3.0")
public class ItemDefinitionsDOMRenderer_3_3_0 implements ItemDefinitionsResource.Renderer {

    private Element rootElem;
    private Element itemDefinitionsElem;

    public void start() {
        rootElem = new Element("Representation");
        itemDefinitionsElem = new Element("ItemDefinitions");
        rootElem.addContent(itemDefinitionsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newItemDefinition(ItemDefinitionResource.Renderer renderer) {
        itemDefinitionsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("ItemDefinition").detach());
    }

    public void setTruncated(boolean truncated) {
        itemDefinitionsElem.setAttribute("truncated", "" + truncated);
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
