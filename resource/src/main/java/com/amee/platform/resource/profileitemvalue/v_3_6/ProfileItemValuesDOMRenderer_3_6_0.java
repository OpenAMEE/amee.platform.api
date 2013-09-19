package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import com.amee.platform.resource.profileitemvalue.ProfileItemValuesResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValuesDOMRenderer_3_6_0 implements ProfileItemValuesResource.Renderer {

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
    public void newProfileItemValue(ProfileItemValueResource.Renderer renderer) {
        valuesElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Value").detach());
    }

    @Override
    public String getMediaType() {
        return "application/xml";
    }

    @Override
    public Object getObject() {
        return new Document(rootElem);
    }

}
