package com.amee.platform.service.v3.itemvaluedefinition.v_3_1;

import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.service.v3.itemvaluedefinition.ItemValueDefinitionRenderer;
import org.jdom.Document;
import org.jdom.Element;

public class ItemValueDefinitionDOMRenderer implements ItemValueDefinitionRenderer {

    private ItemValueDefinition itemValueDefinition;
    private Element rootElem;
    private Element itemValueDefinitionElem;

    public ItemValueDefinitionDOMRenderer() {
        this(true);
    }

    public ItemValueDefinitionDOMRenderer(boolean start) {
        super();
        if (start) {
            start();
        }
    }

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void newItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
        itemValueDefinitionElem = new Element("ItemValueDefinition");
        if (rootElem != null) {
            rootElem.addContent(itemValueDefinitionElem);
        }
    }

    @Override
    public void addBasic() {
        itemValueDefinitionElem.setAttribute("uid", itemValueDefinition.getUid());
    }

    @Override
    public void addName() {
        itemValueDefinitionElem.addContent(new Element("Name").setText(itemValueDefinition.getName()));
    }

    @Override
    public void addPath() {
        itemValueDefinitionElem.addContent(new Element("Path").setText(itemValueDefinition.getPath()));
    }

    @Override
    public void addAudit() {
        itemValueDefinitionElem.setAttribute("status", itemValueDefinition.getStatus().getName());
        itemValueDefinitionElem.setAttribute("created", DATE_FORMAT.print(itemValueDefinition.getCreated().getTime()));
        itemValueDefinitionElem.setAttribute("modified", DATE_FORMAT.print(itemValueDefinition.getModified().getTime()));
    }

    @Override
    public void addWikiDoc() {
        itemValueDefinitionElem.addContent(new Element("WikiDoc").setText(itemValueDefinition.getWikiDoc()));
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        Element e = new Element("ItemDefinition");
        itemValueDefinitionElem.addContent(e);
        e.setAttribute("uid", itemDefinition.getUid());
        e.addContent(new Element("Name").setText(itemDefinition.getName()));
    }

    @Override
    public void addUsages() {
        Element itemValueUsagesElem = new Element("Usages");
        itemValueDefinitionElem.addContent(itemValueUsagesElem);
        for (ItemValueUsage itemValueUsage : itemValueDefinition.getItemValueUsages()) {
            Element valueElem = new Element("Usage");
            valueElem.addContent(new Element("Name").setText(itemValueUsage.getName()));
            valueElem.addContent(new Element("Type").setText(itemValueUsage.getType().toString()));
            itemValueUsagesElem.addContent(valueElem);
        }
    }

    public Element getItemValueDefinitionElement() {
        return itemValueDefinitionElem;
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
