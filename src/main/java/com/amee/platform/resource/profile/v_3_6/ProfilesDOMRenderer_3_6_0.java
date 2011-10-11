package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.platform.resource.profile.ProfilesResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfilesDOMRenderer_3_6_0 implements ProfilesResource.Renderer {

    protected Element rootElem;
    protected Element profilesElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        profilesElem = new Element("Profiles");
        rootElem.addContent(profilesElem);
    }

    @Override
    public void newProfile(ProfileResource.Renderer renderer) {
        profilesElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Profile").detach());
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
    public void setTruncated(boolean truncated) {
        
        // TODO
    }
}
