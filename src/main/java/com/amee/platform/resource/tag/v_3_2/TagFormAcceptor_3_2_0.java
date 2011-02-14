package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ObjectType;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagValidationHelper;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.invalidation.InvalidationService;
import com.amee.service.tag.TagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagFormAcceptor_3_2_0 implements TagResource.FormAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private TagService tagService;

    @Autowired
    private TagResourceService tagResourceService;

    @Autowired
    private TagValidationHelper validationHelper;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private InvalidationService invalidationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Never allow Tag to be updated via a tagged entity.
        if (tagResourceService.getEntity(requestWrapper) != null) {
            throw new IllegalStateException("Tags cannot be updated via a tagged entity.");
        }
        // Get Tag identifier.
        String tagIdentifier = requestWrapper.getAttributes().get("tagIdentifier");
        if (tagIdentifier != null) {
            // Get Tag.
            Tag tag = tagService.getTagByIdentifier(tagIdentifier);
            if (tag != null) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForModify(
                        requestWrapper.getAttributes().get("activeUserUid"), tag);
                // Handle the Tag update (entity updated via validation binding).
                validationHelper.setTag(tag);
                if (validationHelper.isValid(requestWrapper.getFormParameters())) {
                    // Invalidation.
                    for (EntityTag entityTag : tagService.getEntityTagsForTag(ObjectType.DC, tag)) {
                        invalidationService.add(entityTag.getEntityReference());
                    }
                    // Woo!
                    return ResponseHelper.getOK(requestWrapper);
                } else {
                    throw new ValidationException(validationHelper.getValidationResult());
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("tagIdentifier");
        }
    }
}