package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsDOMRenderer_3_6_0 implements ProfileItemsResource.Renderer {

    protected Element rootElem;
    protected Element profileItemsElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        profileItemsElem = new Element("Items");
        rootElem.addContent(profileItemsElem);
    }

    @Override
    public void newProfileItem(ProfileItemResource.Renderer renderer) {
        profileItemsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Item").detach());
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void setTruncated(boolean truncated) {
        profileItemsElem.setAttribute("truncated", truncated + "");
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
