package com.amee.platform.resource.dataitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;
import com.amee.platform.resource.dataitemvalue.DataItemValuesHistoryResource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemValuesHistoryDOMRenderer_3_6_0 implements DataItemValuesHistoryResource.Renderer {

    private Element rootElem;
    private Element valuesElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        valuesElem = new Element("Values");
        rootElem.addContent(valuesElem);
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

    @Override
    public void newDataItemValue(DataItemValueHistoryResource.Renderer renderer) {
        valuesElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Value").detach());
    }

    @Override
    public void setTruncated(boolean truncated) {
        valuesElem.setAttribute("truncated", "" + truncated);
    }

}
