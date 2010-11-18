package com.amee.platform.resource.tag.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagValidationHelper;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.0.0")
public class TagsFormAcceptor_3_0_0 implements TagsResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private TagValidationHelper validationHelper;

    @Transactional(rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Create new Tag.
        Tag tag = new Tag();
        validationHelper.setTag(tag);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            // Swap tag with existing tag if it exists.
            Tag existingTag = tagService.getTagByTag(tag.getTag());
            if (existingTag == null) {
                // Save new Tag.
                log.debug("handle() Use new Tag.");
                tagService.persist(tag);
            } else {
                // Use existing tag.
                log.debug("handle() Use existing Tag.");
                tag = existingTag;
            }
            // Deal with an entity if present.
            IAMEEEntityReference entity = tagResourceService.getEntity(requestWrapper);
            if (entity != null) {
                // Find existing EntityTag.
                EntityTag entityTag = tagService.getEntityTag(entity, tag.getTag());
                if (entityTag == null) {
                    // Create and save EntityTag.
                    log.debug("handle() Use new EntityTag.");
                    entityTag = new EntityTag(entity, tag);
                    tagService.persist(entityTag);
                } else {
                    log.debug("handle() EntityTag already exists.");
                }
            } else {
                log.debug("handle() No entity.");
            }
            // Woo!
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}