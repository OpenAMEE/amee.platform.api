package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionRenderer;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("itemValueDefinitionDOMRenderer_3_1_0")
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionDOMRenderer implements ItemValueDefinitionRenderer {

    private ItemValueDefinition itemValueDefinition;
    private Element rootElem;
    private Element itemValueDefinitionElem;

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
    public void addValue() {
        itemValueDefinitionElem.addContent(new Element("Value").setText(itemValueDefinition.getValue()));
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

    @Override
    public void addChoices() {
        itemValueDefinitionElem.addContent(new Element("Choices").setText(itemValueDefinition.getChoices()));
    }

    @Override
    public void addUnits() {
        if (itemValueDefinition.hasUnit()) {
            itemValueDefinitionElem.addContent(new Element("Unit").setText(itemValueDefinition.getUnit().toString()));
        }
        if (itemValueDefinition.hasPerUnit()) {
            itemValueDefinitionElem.addContent(new Element("PerUnit").setText(itemValueDefinition.getPerUnit().toString()));
        }
    }

    @Override
    public void addFlags() {
        itemValueDefinitionElem.addContent(new Element("DrillDown").setText(Boolean.toString(itemValueDefinition.isDrillDown())));
        itemValueDefinitionElem.addContent(new Element("FromData").setText(Boolean.toString(itemValueDefinition.isFromData())));
        itemValueDefinitionElem.addContent(new Element("FromProfile").setText(Boolean.toString(itemValueDefinition.isFromProfile())));
    }

    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
