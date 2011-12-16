package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.auth.User;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.platform.resource.profile.ProfilesFilterValidationHelper;
import com.amee.platform.resource.profile.ProfilesResource;
import com.amee.platform.search.ProfilesFilter;
import com.amee.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfilesBuilder_3_6_0 implements ProfilesResource.Builder {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ProfilesFilterValidationHelper validationHelper;

    private ProfilesResource.Renderer profilesRenderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Set up filter and validate
        ProfilesFilter filter = new ProfilesFilter();
        validationHelper.setProfilesFilter(filter);
        if (validationHelper.isValid(requestWrapper.getQueryParameters())) {

            // Start Renderer
            ProfilesResource.Renderer renderer = getRenderer(requestWrapper);
            renderer.start();

            // Add Profiles
            // TODO: Should we include the user ID in the filter rather than a separate param?
            String userUid = requestWrapper.getAttributes().get("activeUserUid");
            ResultsWrapper<Profile> profiles = profileService.getProfilesByUserUid(userUid, filter);

            // Have the results been truncated?
            renderer.setTruncated(profiles.isTruncated());

            // Delegate rendering of each profile to the ProfileBuilder.
            ProfileResource.Builder profileBuilder = getProfileBuilder(requestWrapper);
            for (Profile profile : profiles.getResults()) {

                // Render the profile
                profileBuilder.handle(requestWrapper, profile);

                // Uses the Profile element from the ProfileRender
                renderer.newProfile(profileBuilder.getRenderer(requestWrapper));
            }

            renderer.ok();
            return renderer.getObject();
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }

    @Override
    public ProfilesResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (profilesRenderer == null) {
            profilesRenderer = (ProfilesResource.Renderer) resourceBeanFinder.getRenderer(
                ProfilesResource.Renderer.class, requestWrapper);
        }
        return profilesRenderer;
    }

    private ProfileResource.Builder getProfileBuilder(RequestWrapper requestWrapper) {
        return (ProfileResource.Builder) resourceBeanFinder.getBuilder(ProfileResource.Builder.class, requestWrapper);
    }
}
