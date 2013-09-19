package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.v_3_2.TagsDOMRenderer_3_2_0;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class TagsDOMRenderer_3_0_0 extends TagsDOMRenderer_3_2_0 {

    @Override
    public void newTag(Tag tag) {
        Element tagElem = new Element("Tag");
        tagElem.addContent(new Element("Tag").setText(tag.getTag()));
        if (tag.hasCount()) {
            tagElem.addContent(new Element("Count").setText("" + tag.getCount()));
        }
        tagsElem.addContent(tagElem);
    }
}
