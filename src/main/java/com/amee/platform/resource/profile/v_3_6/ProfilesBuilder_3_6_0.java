package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.auth.User;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.platform.resource.profile.ProfilesResource;
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

    private ProfilesResource.Renderer profilesRenderer;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Authorisation? We only get the current user's profiles so should need authorisation here.
        // We will for a single profile?

        // Start Renderer
        ProfilesResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Add Profiles
        ProfileResource.Builder profileBuilder = getProfileBuilder(requestWrapper);
        for (Profile profile : profileService.getProfilesByUserUid(requestWrapper.getAttributes().get("activeUserUid"))) {
            profileBuilder.handle(requestWrapper, profile);
            renderer.newProfile(profileBuilder.getRenderer(requestWrapper));
        }

        renderer.ok();
        return renderer.getObject();
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
