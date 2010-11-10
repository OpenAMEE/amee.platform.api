package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.RendererBeanFinder;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("tagsBuilder_3_0_0")
@Scope("prototype")
@Since("3.0.0")
public class TagsBuilder implements ResourceBuilder {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private RendererBeanFinder rendererBeanFinder;

    private TagsResource.TagsRenderer tagsRenderer;

    @Transactional(readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        handle(requestWrapper, tagResourceService.getEntity(requestWrapper));
        TagsResource.TagsRenderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, IAMEEEntityReference entity) {
        TagsResource.TagsRenderer renderer = getRenderer(requestWrapper);
        renderer.start();
        for (Tag tag : tagService.getTags(entity)) {
            renderer.newTag(tag);
        }
    }

    public TagsResource.TagsRenderer getRenderer(RequestWrapper requestWrapper) {
        if (tagsRenderer == null) {
            tagsRenderer = (TagsResource.TagsRenderer) rendererBeanFinder.getRenderer(TagsResource.TagsRenderer.class, requestWrapper);
        }
        return tagsRenderer;
    }
}