package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagDOMRenderer_3_2_0 implements TagResource.Renderer {

    private Tag tag;
    private Element rootElem;
    private Element tagElem;

    @Override
    public void start() {
        rootElem = new Element("Representation");
    }

    @Override
    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    @Override
    public void newTag(Tag tag) {
        this.tag = tag;
        tagElem = new Element("Tag");
        if (rootElem != null) {
            rootElem.addContent(tagElem);
        }
    }

    @Override
    public void addBasic() {
        tagElem.setAttribute("uid", tag.getUid());
        tagElem.addContent(new Element("Tag").setText(tag.getTag()));
        if (tag.hasCount()) {
            tagElem.addContent(new Element("Count").setText("" + tag.getCount()));
        }
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
