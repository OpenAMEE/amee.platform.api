package com.amee.platform.resource.search;

import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class SearchDOMRenderer implements SearchRenderer {

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

    public void newDataCategory(DataCategoryRenderer dataCategoryRenderer) {
        resultsElem.addContent(((Document) dataCategoryRenderer.getObject()).getRootElement().getChild("Category").detach());
    }

    public void newDataItem(DataItemRenderer dataItemRenderer) {
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
