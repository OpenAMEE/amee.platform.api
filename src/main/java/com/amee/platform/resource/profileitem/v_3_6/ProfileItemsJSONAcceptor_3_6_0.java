package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.NotFoundException;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Receives and processes batch updates to Profile Items submitted in JSON format.
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsJSONAcceptor_3_6_0 implements ProfileItemsResource.JSONAcceptor {
    
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private DataService dataService;

    @Autowired
    private ProfileItemService profileItemService;

    @Autowired
    private ProfileService profileService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) {

        // Entities (in an ordered map)
        Map<String, String> entities = new LinkedHashMap<String, String>();

        // Get entities
        Profile profile = resourceService.getProfile(requestWrapper);
        JSONObject requestBodyJSON = requestWrapper.getBodyAsJSONObject();

        try {
            if (requestBodyJSON.has("profileItems")) {
                JSONArray profileItemsJSON = requestBodyJSON.getJSONArray("profileItems");

                // Handle each profile item in request
                for (int i = 0; i < profileItemsJSON.length(); i++) {
                    JSONObject profileItemJSON = profileItemsJSON.getJSONObject(i);

                    DataItem dataItem = null;
                    String key, value;
                    Map<String, String> parameters = new HashMap<String, String>();

                    Iterator<String> keys = profileItemJSON.keys();
                    while (keys.hasNext()) {
                        key = keys.next();
                        value = profileItemJSON.getString(key);
                        if ("dataItemUid".equalsIgnoreCase(key)) {
                            dataItem = dataItemService.getItemByUid(value);
                        } else if ("category".equalsIgnoreCase(key)) {
                            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(value);
                            dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
                        } else {
                            parameters.put(key, value);
                        }
                    }

                    // Check that we can correctly identify the data item, if not then we cannot continue
                    if (dataItem == null) {
                        throw new NotFoundException();
                    }

                    // Authorised?
                    resourceAuthorizationService.ensureAuthorizedForAccept(requestWrapper.getAttributes().get("activeUserUid"), profile);

                    // Validate and handle this profile item
                    ProfileItem profileItem = new ProfileItem(profile, dataItem);

                    ProfileItemResource.ProfileItemValidator validator = getValidator(requestWrapper);
                    validator.setObject(profileItem);

                    if (validator.isValid(parameters)) {
                        profileItemService.updateProfileItemValues(profileItem);
                        profileItemService.clearItemValues();
                        profileService.clearCaches(profileItem.getProfile());
                    } else {
                        throw new ValidationException(validator.getValidationResult());
                    }
                    
                    // This will generate all profile item values.
                    profileItemService.persist(profileItem);

                    // Aggregate the results
                    String location = "/" + requestWrapper.getVersion() + "/profiles/" +
                        requestWrapper.getAttributes().get("profileIdentifier") + "/items/" + profileItem.getUid();
                    entities.put(profileItem.getUid(), location);
                }
            }

            return ResponseHelper.getBatchProfileOK(requestWrapper, entities);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected ProfileItemResource.ProfileItemValidator getValidator(RequestWrapper requestWrapper) {
        return (ProfileItemResource.ProfileItemValidator) resourceBeanFinder.getValidator(
            ProfileItemResource.ProfileItemValidator.class, requestWrapper);
    }
}
