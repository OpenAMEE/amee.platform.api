package com.amee.platform.resource.dataitem;

import com.amee.platform.resource.dataitem.v_3_1.DataItemDOMRenderer;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataItemsDOMRenderer implements DataItemsRenderer {

    private DataItemDOMRenderer dataItemRenderer;
    private Element rootElem;
    private Element itemsElem;

    public DataItemsDOMRenderer() {
        super();
        dataItemRenderer = new DataItemDOMRenderer(false);
        start();
    }

    public void start() {
        rootElem = new Element("Representation");
        itemsElem = new Element("Items");
        rootElem.addContent(itemsElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newDataItem() {
        itemsElem.addContent(dataItemRenderer.getDataItemElement());
    }

    public void setTruncated(boolean truncated) {
        itemsElem.setAttribute("truncated", "" + truncated);
    }

    public DataItemRenderer getDataItemRenderer() {
        return dataItemRenderer;
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Object getObject() {
        return new Document(rootElem);
    }
}
