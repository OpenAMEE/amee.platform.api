package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.profile.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileRemover_3_6_0 implements ProfileResource.Remover {

    @Autowired
    ProfileService profileService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entity
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForRemove(
            requestWrapper.getAttributes().get("activeUserUid"), profile);

        // Remove the profile
        profileService.remove(profile);
        return ResponseHelper.getOK(requestWrapper, null, profile.getUid());
    }

}
