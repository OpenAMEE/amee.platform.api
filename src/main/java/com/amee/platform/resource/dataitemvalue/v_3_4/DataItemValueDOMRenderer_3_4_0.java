package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.domain.IDataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueDOMRenderer_3_4_0 implements DataItemValueResource.Renderer {

    @Autowired
    protected IDataItemService dataItemService;

    protected BaseDataItemValue dataItemValue;
    protected Element rootElem;
    protected Element dataItemValueElem;

    public void start() {
        rootElem = new Element("Representation");
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
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
        dataItemValueElem.setAttribute("history", Boolean.toString(dataItemValue.isHistoryAvailable()));
        dataItemValueElem.addContent(new Element("Value").setText(dataItemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(dataItemValue.getClass())) {
            NumberValue nv = (NumberValue) dataItemValue;
            if (nv.hasUnit()) {
                dataItemValueElem.addContent(new Element("Unit").setText(nv.getUnit().toString()));
                if (nv.hasPerUnit()) {
                    dataItemValueElem.addContent(new Element("PerUnit").setText(nv.getPerUnit().toString()));
                    dataItemValueElem.addContent(new Element("CompoundUnit").setText(nv.getCompoundUnit().toString()));
                }
            }
        }
        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            HistoryValue hv = (HistoryValue) dataItemValue;
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(hv.getStartDate().getTime())));
        } else {
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(IDataItemService.EPOCH.getTime())));
        }
    }

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

    public String getMediaType() {
        return "application/xml";
    }

    public Object getObject() {
        return new Document(rootElem);
    }
}
