package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.service.tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagRemover_3_2_0 implements TagResource.Remover {

    @Autowired
    private TagService tagService;

    public Object handle(RequestWrapper requestWrapper) {
        String tagIdentifier = requestWrapper.getAttributes().get("tagIdentifier");
        if (tagIdentifier != null) {
            Tag tag = tagService.getTagByIdentifier(tagIdentifier);
            if (tag != null) {
                tagService.remove(tag);
                return ResponseHelper.getOK(requestWrapper);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("tagIdentifier");
        }
    }
}