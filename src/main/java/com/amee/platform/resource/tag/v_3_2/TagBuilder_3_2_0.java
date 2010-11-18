package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagBuilder_3_2_0 implements TagResource.Builder {

    @Autowired
    private TagService tagService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private TagResource.Renderer renderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get Tag identifier.
        String tagIdentifier = requestWrapper.getAttributes().get("tagIdentifier");
        if (tagIdentifier != null) {
            // Get Tag.
            Tag tag = tagService.getTagByIdentifier(tagIdentifier);
            if (tag != null) {
                // Handle the Tag.
                handle(requestWrapper, tag);
                TagResource.Renderer renderer = getRenderer(requestWrapper);
                renderer.ok();
                return renderer.getObject();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("tagIdentifier");
        }
    }

    protected void handle(RequestWrapper requestWrapper, Tag tag) {

        TagResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // New Tag & basic.
        renderer.newTag(tag);
        renderer.addBasic();
    }

    public TagResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (TagResource.Renderer) resourceBeanFinder.getRenderer(TagResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}