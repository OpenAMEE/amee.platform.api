package com.amee.platform.resource.search;

import com.amee.platform.resource.datacategory.DataCategoryDOMRenderer;
import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import com.amee.platform.resource.dataitem.v_3_1.DataItemDOMRenderer;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class SearchDOMRenderer implements SearchRenderer {

    private DataCategoryDOMRenderer dataCategoryRenderer;
    private DataItemDOMRenderer dataItemRenderer;
    private Element rootElem;
    private Element resultsElem;

    public SearchDOMRenderer() {
        super();
        this.dataCategoryRenderer = new DataCategoryDOMRenderer(false);
        this.dataItemRenderer = new DataItemDOMRenderer(false);
        start();
    }

    public void start() {
        rootElem = new Element("Representation");
        resultsElem = new Element("Results");
        rootElem.addContent(resultsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataCategory() {
        resultsElem.addContent(dataCategoryRenderer.getDataCategoryElement());
    }

    public void newDataItem() {
        resultsElem.addContent(dataItemRenderer.getDataItemElement());
    }

    public void setTruncated(boolean truncated) {
        resultsElem.setAttribute("truncated", "" + truncated);
    }

    public DataCategoryRenderer getDataCategoryRenderer() {
        return dataCategoryRenderer;
    }

    public DataItemRenderer getDataItemRenderer() {
        return dataItemRenderer;
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
