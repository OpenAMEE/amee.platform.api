package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.datacategory.DataCategoriesResource;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoriesDOMRenderer_3_0_0 implements DataCategoriesResource.Renderer {

    private Element rootElem;
    private Element categoriesElem;

    public void start() {
        rootElem = new Element("Representation");
        categoriesElem = new Element("Categories");
        rootElem.addContent(categoriesElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataCategory(DataCategoryResource.Renderer renderer) {
        categoriesElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Category").detach());
    }

    public void setTruncated(boolean truncated) {
        categoriesElem.setAttribute("truncated", "" + truncated);
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
