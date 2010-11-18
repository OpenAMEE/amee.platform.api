package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagDOMRenderer_3_2_0 implements TagResource.Renderer {

    private Tag tag;
    private Element rootElem;
    private Element tagElem;

    public void start() {
        rootElem = new Element("Representation");
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public void newTag(Tag tag) {
        this.tag = tag;
        tagElem = new Element("Tag");
        if (rootElem != null) {
            rootElem.addContent(tagElem);
        }
    }

    public void addBasic() {
        tagElem.setAttribute("uid", tag.getUid());
        tagElem.addContent(new Element("Name").setText(tag.getTag()));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
