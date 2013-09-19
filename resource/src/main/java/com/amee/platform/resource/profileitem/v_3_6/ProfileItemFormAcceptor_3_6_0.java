package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ProfileItemService;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.profile.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemFormAcceptor_3_6_0 implements ProfileItemResource.FormAcceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ProfileItemService profileItemService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ProfileService profileService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {
        
        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        ProfileItem profileItem = resourceService.getProfileItem(requestWrapper, profile);
        
        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForModify(
            requestWrapper.getAttributes().get("activeUserUid"), profileItem);
        
        // Handle the update
        return handle(requestWrapper, profileItem);
    }

    protected Object handle(RequestWrapper requestWrapper, ProfileItem profileItem) {
        ProfileItemResource.ProfileItemValidator validator = getValidator(requestWrapper);
        validator.setObject(profileItem);
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            profileItemService.updateProfileItemValues(profileItem);
            profileItemService.clearItemValues();
            profileService.clearCaches(profileItem.getProfile());
            return ResponseHelper.getOK(requestWrapper, null, profileItem.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected ProfileItemResource.ProfileItemValidator getValidator(RequestWrapper requestWrapper) {
        return(ProfileItemResource.ProfileItemValidator)
            resourceBeanFinder.getValidator(
                ProfileItemResource.ProfileItemValidator.class, requestWrapper);
    }

}
