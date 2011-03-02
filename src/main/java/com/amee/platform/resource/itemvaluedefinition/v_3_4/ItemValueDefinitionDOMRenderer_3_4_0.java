package com.amee.platform.resource.itemvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.domain.APIVersion;
import com.amee.domain.ValueDefinition;
import com.amee.domain.ValueType;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemValueDefinitionDOMRenderer_3_4_0 implements ItemValueDefinitionResource.Renderer {

    protected ItemValueDefinition itemValueDefinition;
    protected Element rootElem;
    protected Element itemValueDefinitionElem;

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
    public void addValueDefinition(ValueDefinition valueDefinition) {
        Element e = new Element("ValueDefinition");
        itemValueDefinitionElem.addContent(e);
        e.setAttribute("uid", valueDefinition.getUid());
        e.addContent(new Element("Name").setText(valueDefinition.getName()));
        e.addContent(new Element("ValueType").setText(
                valueDefinition.getValueType().equals(ValueType.DOUBLE) ? "DOUBLE" : valueDefinition.getValueType().getName()));
    }

    @Override
    public void addUsages() {
        Collection<ItemValueUsage> itemDefinitionUsages = itemValueDefinition.getItemDefinition().getItemValueUsages();
        Element itemValueUsagesElem = new Element("Usages");
        itemValueDefinitionElem.addContent(itemValueUsagesElem);
        for (ItemValueUsage itemValueUsage : itemValueDefinition.getItemValueUsages()) {
            Element valueElem = new Element("Usage");
            valueElem.addContent(new Element("Name").setText(itemValueUsage.getName()));
            valueElem.addContent(new Element("Type").setText(itemValueUsage.getType().toString()));
            valueElem.setAttribute("active", Boolean.toString(itemDefinitionUsages.contains(itemValueUsage)));
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
            itemValueDefinitionElem.addContent(new Element("Unit").setText(itemValueDefinition.getUnitAsAmountUnit().toString()));
        }
        if (itemValueDefinition.hasPerUnit()) {
            itemValueDefinitionElem.addContent(new Element("PerUnit").setText(itemValueDefinition.getPerUnitAsAmountPerUnit().toString()));
        }
    }

    @Override
    public void addFlags() {
        itemValueDefinitionElem.addContent(new Element("DrillDown").setText(Boolean.toString(itemValueDefinition.isDrillDown())));
        itemValueDefinitionElem.addContent(new Element("FromData").setText(Boolean.toString(itemValueDefinition.isFromData())));
        itemValueDefinitionElem.addContent(new Element("FromProfile").setText(Boolean.toString(itemValueDefinition.isFromProfile())));
    }

    @Override
    public void addVersions() {
        Element versionsElem = new Element("Versions");
        itemValueDefinitionElem.addContent(versionsElem);
        for (APIVersion apiVersion : itemValueDefinition.getAPIVersions()) {
            Element versionElem = new Element("Version");
            versionElem.addContent(new Element("Version").setText(apiVersion.getVersion()));
            versionsElem.addContent(versionElem);
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
