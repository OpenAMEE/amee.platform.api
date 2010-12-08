package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagsDOMRenderer_3_2_0 implements TagsResource.Renderer {

    protected Element rootElem;
    protected Element tagsElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
        tagsElem = new Element("Tags");
        rootElem.addContent(tagsElem);
    }

    @Override
    public void newTag(TagResource.Renderer renderer) {
        tagsElem.addContent(((Document) renderer.getObject()).getRootElement().getChild("Tag").detach());
    }

    @Deprecated
    @Override
    public void newTag(Tag tag) {
        throw new UnsupportedOperationException("This method is deprecated since 3.2.0.");
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
    public Document getObject() {
        return new Document(rootElem);
    }
}
