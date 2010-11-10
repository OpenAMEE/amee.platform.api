package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagsResource;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("tagsDOMRenderer_3_0_0")
@Scope("prototype")
@Since("3.0.0")
public class TagsDOMRenderer implements TagsResource.TagsRenderer {

    private Element rootElem;
    private Element tagsElem;

    public void start() {
        rootElem = new Element("Representation");
        tagsElem = new Element("Tags");
        rootElem.addContent(tagsElem);
    }

    public void newTag(Tag tag) {
        Element tagElem = new Element("Tag");
        tagElem.addContent(new Element("Tag").setText(tag.getTag()));
        tagElem.addContent(new Element("Count").setText("" + tag.getCount()));
        tagsElem.addContent(tagElem);
    }

    public void ok() {
        rootElem.addContent(new Element("Status").setText("OK"));
    }

    public String getMediaType() {
        return "application/xml";
    }

    public Document getObject() {
        return new Document(rootElem);
    }
}
