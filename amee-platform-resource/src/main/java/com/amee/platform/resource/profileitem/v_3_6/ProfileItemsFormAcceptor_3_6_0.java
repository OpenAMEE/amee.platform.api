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
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.profile.ProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsFormAcceptor_3_6_0 implements ProfileItemsResource.FormAcceptor {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ProfileItemService profileItemService;

    @Autowired
    private DataService dataService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ProfileService profileService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        DataItem dataItem;
        if (requestWrapper.getFormParameters().get("dataItemUid") != null) {
            dataItem = dataItemService.getItemByUid(requestWrapper.getFormParameters().get("dataItemUid"));
        } else if (requestWrapper.getFormParameters().get("category") != null) {
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(
                requestWrapper.getFormParameters().get("category"));
            dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        } else {

            // TODO: This causes a 500 error.
            throw new MissingAttributeException("dataItemUid");
        }

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
            profileItemService.clearItemValues();
            profileService.clearCaches(profileItem.getProfile());

            return ResponseHelper.getOK(
                requestWrapper,
                "/" + requestWrapper.getVersion() +
                    "/profiles/" + requestWrapper.getAttributes().get("profileIdentifier") +
                    "/items/" + profileItem.getUid(),
                profileItem.getUid());
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
