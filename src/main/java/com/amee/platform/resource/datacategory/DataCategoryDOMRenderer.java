package com.amee.platform.resource.datacategory;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataCategoryDOMRenderer implements DataCategoryRenderer {

    private DataCategory dataCategory;
    private Element rootElem;
    private Element dataCategoryElem;
    private Element tagsElem;

    public void start() {
        rootElem = new Element("Representation");
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
        dataCategoryElem = new Element("Category");
        if (rootElem != null) {
            rootElem.addContent(dataCategoryElem);
        }
    }

    public void addBasic() {
        dataCategoryElem.setAttribute("uid", dataCategory.getUid());
        dataCategoryElem.addContent(new Element("Name").setText(dataCategory.getName()));
        dataCategoryElem.addContent(new Element("WikiName").setText(dataCategory.getWikiName()));
    }

    public void addPath() {
        dataCategoryElem.addContent(new Element("Path").setText(dataCategory.getPath()));
        dataCategoryElem.addContent(new Element("FullPath").setText(dataCategory.getFullPath()));
    }

    public void addParent() {
        if (dataCategory.getDataCategory() != null) {
            dataCategoryElem.addContent(new Element("ParentUid").setText(dataCategory.getDataCategory().getUid()));
            dataCategoryElem.addContent(new Element("ParentWikiName").setText(dataCategory.getDataCategory().getWikiName()));
        }
    }

    public void addAudit() {
        dataCategoryElem.setAttribute("status", dataCategory.getStatus().getName());
        dataCategoryElem.setAttribute("created", DATE_FORMAT.print(dataCategory.getCreated().getTime()));
        dataCategoryElem.setAttribute("modified", DATE_FORMAT.print(dataCategory.getModified().getTime()));
    }

    public void addAuthority() {
        dataCategoryElem.addContent(new Element("Authority").setText(dataCategory.getAuthority()));
    }

    public void addWikiDoc() {
        dataCategoryElem.addContent(new Element("WikiDoc").setText(dataCategory.getWikiDoc()));
    }

    public void addProvenance() {
        dataCategoryElem.addContent(new Element("Provenance").setText(dataCategory.getProvenance()));
    }

    public void addItemDefinition(ItemDefinition itemDefinition) {
        Element e = new Element("ItemDefinition");
        dataCategoryElem.addContent(e);
        e.setAttribute("uid", itemDefinition.getUid());
        e.addContent(new Element("Name").setText(itemDefinition.getName()));
    }

    public void startTags() {
        tagsElem = new Element("Tags");
        dataCategoryElem.addContent(tagsElem);
    }

    public void newTag(Tag tag) {
        Element tagElem = new Element("Tag");
        tagsElem.addContent(tagElem);
        tagElem.addContent(new Element("Tag").setText(tag.getTag()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
