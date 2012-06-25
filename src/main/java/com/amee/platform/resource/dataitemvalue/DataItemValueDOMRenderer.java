package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.BaseDataItemValue;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract Superclass for DOM renderers of DataItemValues and DataItemValueHistories which share almost all code.
 * 
 * @see
 * {@link com.amee.platform.resource.dataitemvalue.v_3_4.DataItemValueDOMRenderer_3_4_0 DataItemValueDOMRenderer_3_4_0},
 * {@link com.amee.platform.resource.dataitemvalue.v_3_6.DataItemValueHistoryDOMRenderer_3_6_0 DataItemValueHistoryDOMRenderer_3_6_0}
 */
public abstract class DataItemValueDOMRenderer implements ResourceRenderer {

    @Autowired
    protected DataItemService dataItemService;

    protected BaseDataItemValue dataItemValue;
    protected Element rootElem;
    protected Element dataItemValueElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }

    public void newDataItemValue(BaseDataItemValue dataItemValue) {
        this.dataItemValue = dataItemValue;
        dataItemValueElem = new Element("Value");
        if (rootElem != null) {
            rootElem.addContent(dataItemValueElem);
        }
    }

    public void addBasic() {
        dataItemValueElem.setAttribute("uid", dataItemValue.getUid());
        if (!isHistoryItem()) {
            dataItemValueElem.setAttribute("history", Boolean.toString(dataItemValue.isHistoryAvailable()));
        }
        dataItemValueElem.addContent(new Element("Value").setText(dataItemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(dataItemValue.getClass())) {
            NumberValue nv = (NumberValue) dataItemValue;
            if (nv.hasUnit()) {
                dataItemValueElem.addContent(new Element("Unit").setText(nv.getCompoundUnit().toString()));
            }
        }
        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            HistoryValue hv = (HistoryValue) dataItemValue;
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(hv.getStartDate().getTime())));
        } else {
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(DataItemService.EPOCH.getTime())));
        }
    }

    /**
     * Indicates whether or not this DataItemValue forms part of a request for a history or not
     * @return  <code>true</code> if this item is part of a history, <code>false</code> otherwise
     */
    public abstract boolean isHistoryItem();

    public void addPath() {
        dataItemValueElem.addContent(new Element("Path").setText(dataItemValue.getPath()));
        dataItemValueElem.addContent(new Element("FullPath").setText(dataItemValue.getFullPath()));
    }

    public void addDataCategory() {
        Element e = new Element("Category");
        dataItemValueElem.addContent(e);
        e.setAttribute("uid", dataItemValue.getDataItem().getDataCategory().getUid());
        e.addContent(new Element("WikiName").setText(dataItemValue.getDataItem().getDataCategory().getWikiName()));
    }

    public void addDataItem() {
        Element e = new Element("Item");
        dataItemValueElem.addContent(e);
        e.setAttribute("uid", dataItemValue.getDataItem().getUid());
    }

    public void addAudit() {
        dataItemValueElem.setAttribute("status", dataItemValue.getStatus().getName());
        dataItemValueElem.setAttribute("created", DATE_FORMAT.print(dataItemValue.getCreated().getTime()));
        dataItemValueElem.setAttribute("modified", DATE_FORMAT.print(dataItemValue.getModified().getTime()));
    }

    public void addItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        Element e = new Element("ItemValueDefinition");
        dataItemValueElem.addContent(e);
        e.setAttribute("uid", itemValueDefinition.getUid());
        e.addContent(new Element("Name").setText(itemValueDefinition.getName()));
        e.addContent(new Element("Path").setText(itemValueDefinition.getPath()));
    }
}
