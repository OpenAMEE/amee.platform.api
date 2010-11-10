package com.amee.platform.resource.search.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.search.SearchResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class SearchDOMRenderer_3_0_0 implements SearchResource.Renderer {

    private Element rootElem;
    private Element resultsElem;

    public void start() {
        rootElem = new Element("Representation");
        resultsElem = new Element("Results");
        rootElem.addContent(resultsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataCategory(DataCategoryResource.Renderer dataCategoryRenderer) {
        resultsElem.addContent(((Document) dataCategoryRenderer.getObject()).getRootElement().getChild("Category").detach());
    }

    public void newDataItem(DataItemResource.Renderer dataItemRenderer) {
        resultsElem.addContent(((Document) dataItemRenderer.getObject()).getRootElement().getChild("Item").detach());
    }

    public void setTruncated(boolean truncated) {
        resultsElem.setAttribute("truncated", "" + truncated);
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
