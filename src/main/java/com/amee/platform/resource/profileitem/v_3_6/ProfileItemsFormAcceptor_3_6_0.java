package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemService;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsFormAcceptor_3_6_0 implements ProfileItemsResource.FormAcceptor {
    
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ProfileItemService profileItemService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        String dataItemUid = requestWrapper.getFormParameters().get("dataItemUid");
        if (dataItemUid == null) {
            throw new MissingAttributeException("dataItemUid");
        }

        DataItem dataItem = dataItemService.getItemByUid(dataItemUid);
        if (dataItem == null) {

            // A better exception here?
            throw new ValidationException();
        }

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(
            requestWrapper.getAttributes().get("activeUserUid"), profile);
        
        ProfileItem profileItem = new ProfileItem(profile, dataItem);

        // This will generate all profile item values.
        profileItemService.persist(profileItem);
        return handle(requestWrapper, profileItem);
    }

    protected Object handle(RequestWrapper requestWrapper, ProfileItem profileItem) {
        ProfileItemResource.ProfileItemValidator validator = getValidator(requestWrapper);
        validator.setObject(profileItem);
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            profileItemService.updateProfileItemValues(profileItem);
            return ResponseHelper.getOK(
                requestWrapper,
                "/" + requestWrapper.getVersion() +
                    "/profiles/" + requestWrapper.getAttributes().get("profileIdentifier") +
                    "/items/" + profileItem.getUid());
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
