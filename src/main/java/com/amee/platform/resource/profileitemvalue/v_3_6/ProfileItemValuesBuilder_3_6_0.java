package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ProfileItemService;
import com.amee.domain.ProfileItemValuesFilter;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import com.amee.platform.resource.profileitemvalue.ProfileItemValuesResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValuesBuilder_3_6_0 implements ProfileItemValuesResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ProfileItemService profileItemService;

    private ProfileItemValuesResource.Renderer renderer;


    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        ProfileItem profileItem = resourceService.getProfileItem(requestWrapper, profile);

        // Authorised for profileItem?
        resourceAuthorizationService.ensureAuthorizedForBuild(
            requestWrapper.getAttributes().get("activeUserUid"), profileItem);
        
        // Create filter
        ProfileItemValuesFilter filter = new ProfileItemValuesFilter();

        // TODO: Update filter to include dates?

        // Create validator
        ProfileItemValuesResource.ProfileItemValuesFilterValidator validator = getValidator(requestWrapper);
        validator.setObject(filter);
        validator.initialise();
        
        // Is the filter valid
        if (validator.isValid(requestWrapper.getQueryParameters())) {
            handle(requestWrapper, profileItem, filter);
            ProfileItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validator.getValidationResult());
        }

    }

    @Override
    public void handle(RequestWrapper requestWrapper, ProfileItem profileItem, ProfileItemValuesFilter filter) {

        // Setup renderer
        ProfileItemValuesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();
        
        // Add profile item values to renderer and build
        ProfileItemValueResource.Builder profileItemValueBuilder = getProfileItemValueBuilder(requestWrapper);
        for (BaseItemValue itemValue : profileItemService.getItemValues(profileItem)) {
            BaseProfileItemValue profileItemValue = (BaseProfileItemValue) itemValue;
            profileItemValueBuilder.handle(requestWrapper, profileItemValue);
            renderer.newProfileItemValue(profileItemValueBuilder.getRenderer(requestWrapper));
        }
    }

    @Override
    public ProfileItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (ProfileItemValuesResource.Renderer) resourceBeanFinder.getRenderer(
                ProfileItemValuesResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    @Override
    public ProfileItemValueResource.Builder getProfileItemValueBuilder(RequestWrapper requestWrapper) {
        return (ProfileItemValueResource.Builder)
            resourceBeanFinder.getBuilder(ProfileItemValueResource.Builder.class, requestWrapper);
    }

    @Override
    public ProfileItemValuesResource.ProfileItemValuesFilterValidator getValidator(RequestWrapper requestWrapper) {
        return (ProfileItemValuesResource.ProfileItemValuesFilterValidator)
            resourceBeanFinder.getValidator(
                ProfileItemValuesResource.ProfileItemValuesFilterValidator.class, requestWrapper);
    }
}
