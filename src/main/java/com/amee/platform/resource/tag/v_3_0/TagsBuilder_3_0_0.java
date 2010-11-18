package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class TagsBuilder_3_0_0 implements TagsResource.Builder {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private TagsResource.Renderer tagsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        handle(requestWrapper, tagResourceService.getEntity(requestWrapper));
        TagsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity) {
        TagsResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        for (Tag tag : tagService.getTags(entity)) {
            renderer.newTag(tag);
        }
    }

    public TagsResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (tagsRenderer == null) {
            tagsRenderer = (TagsResource.Renderer) resourceBeanFinder.getRenderer(TagsResource.Renderer.class, requestWrapper);
        }
        return tagsRenderer;
    }
}