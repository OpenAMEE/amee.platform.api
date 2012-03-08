package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.ProfileItemService;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemRemover_3_6_0 implements ProfileItemResource.Remover {

    @Autowired
    ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ProfileItemService profileItemService;
    
    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {
        
        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        ProfileItem profileItem = resourceService.getProfileItem(requestWrapper, profile);
        
        // Authorised
        resourceAuthorizationService.ensureAuthorizedForRemove(
            requestWrapper.getAttributes().get("activeUserUid"), profileItem);
        
        // Handle profile item removal
        profileItemService.remove(profileItem);
        return ResponseHelper.getOK(requestWrapper, null, profileItem.getUid());
    }
}
