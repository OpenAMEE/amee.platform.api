package com.amee.platform.resource.itemdefinition;

import com.amee.base.domain.Since;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueUsage;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("itemDefinitionDOMRenderer_3_1_0")
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionDOMRenderer implements ItemDefinitionRenderer {

    private ItemDefinition itemDefinition;
    private Element rootElem;
    private Element itemDefinitionElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void newItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
        itemDefinitionElem = new Element("ItemDefinition");
        if (rootElem != null) {
            rootElem.addContent(itemDefinitionElem);
        }
    }

    @Override
    public void addBasic() {
        itemDefinitionElem.setAttribute("uid", itemDefinition.getUid());
    }

    @Override
    public void addAudit() {
        itemDefinitionElem.setAttribute("status", itemDefinition.getStatus().getName());
        itemDefinitionElem.setAttribute("created", DATE_FORMAT.print(itemDefinition.getCreated().getTime()));
        itemDefinitionElem.setAttribute("modified", DATE_FORMAT.print(itemDefinition.getModified().getTime()));
    }

    @Override
    public void addName() {
        itemDefinitionElem.addContent(new Element("Name").setText(itemDefinition.getName()));
    }

    @Override
    public void addDrillDown() {
        itemDefinitionElem.addContent(new Element("DrillDown").setText(itemDefinition.getDrillDown()));
    }

    @Override
    public void addUsages() {
        Set<ItemValueUsage> allItemValueUsages = itemDefinition.getAllItemValueUsages();
        Element itemValueUsagesElem = new Element("Usages");
        itemDefinitionElem.addContent(itemValueUsagesElem);
        for (ItemValueUsage itemValueUsage : itemDefinition.getItemValueUsages()) {
            Element valueElem = new Element("Usage");
            valueElem.addContent(new Element("Name").setText(itemValueUsage.getName()));
            valueElem.setAttribute("present", Boolean.toString(allItemValueUsages.contains(itemValueUsage)));
            itemValueUsagesElem.addContent(valueElem);
        }
    }

    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }
}
