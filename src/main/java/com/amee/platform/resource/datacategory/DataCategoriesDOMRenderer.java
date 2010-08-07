package com.amee.platform.resource.datacategory;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataCategoriesDOMRenderer implements DataCategoriesRenderer {

    private DataCategoryDOMRenderer dataCategoryRenderer;
    private Element rootElem;
    private Element categoriesElem;

    public DataCategoriesDOMRenderer() {
        super();
        this.dataCategoryRenderer = new DataCategoryDOMRenderer(false);
        start();
    }

    public void start() {
        rootElem = new Element("Representation");
        categoriesElem = new Element("Categories");
        rootElem.addContent(categoriesElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataCategory() {
        categoriesElem.addContent(dataCategoryRenderer.getDataCategoryElement());
    }

    public void setTruncated(boolean truncated) {
        categoriesElem.setAttribute("truncated", "" + truncated);
    }

    public DataCategoryRenderer getDataCategoryRenderer() {
        return dataCategoryRenderer;
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
