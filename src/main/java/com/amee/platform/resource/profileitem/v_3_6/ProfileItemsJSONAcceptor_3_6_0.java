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

        // Result entities (in an ordered map)
        Map<String, String> results = new LinkedHashMap<String, String>();

        // Get profile
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(requestWrapper.getAttributes().get("activeUserUid"), profile);

        // Get request body
        JSONObject requestBodyJSON = requestWrapper.getBodyAsJSONObject();

        try {
            if (requestBodyJSON.has("profileItems")) {
                // Get list of profile items from request body
                JSONArray profileItemsJSON = requestBodyJSON.getJSONArray("profileItems");

                // Handle each profile item in request
                for (int i = 0; i < profileItemsJSON.length(); i++) {
                    JSONObject profileItemJSON = profileItemsJSON.getJSONObject(i);

                    DataItem dataItem = null;
                    ProfileItem profileItem = null;
                    String key, value;
                    Map<String, String> parameters = new HashMap<String, String>();

                    Iterator<String> keys = profileItemJSON.keys();
                    while (keys.hasNext()) {
                        key = keys.next();
                        value = profileItemJSON.getString(key);

                        // Parse JSON keys for this new profile item
                        if ("dataItemUid".equalsIgnoreCase(key)) {
                            dataItem = dataItemService.getItemByUid(value);

                        } else if ("category".equalsIgnoreCase(key)) {
                            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(value);
                            dataItem = resourceService.getDataItem(requestWrapper, dataCategory);

                        } else if ("profileItemUid".equalsIgnoreCase(key)) {
                            profileItem = profileItemService.getItemByUid(value);

                        } else {
                            parameters.put(key, value);
                        }
                    }

                    // Make a new profile item if a data item was specified in the request
                    if (dataItem != null) {
                        profileItem = new ProfileItem(profile, dataItem);
                    }

                    // If we haven't been able to find an existing profile item or create a new one from the specified data item, this is an error
                    if (profileItem == null) {
                        throw new NotFoundException();
                    }

                    // Validate and handle this profile item
                    ProfileItemResource.ProfileItemValidator validator = getValidator(requestWrapper);
                    validator.setObject(profileItem);
                    validator.initialise();

                    if (validator.isValid(parameters)) {
                        profileItemService.updateProfileItemValues(profileItem);
                        profileItemService.clearItemValues();
                        profileService.clearCaches(profileItem.getProfile());
                    } else {
                        throw new ValidationException(validator.getValidationResult());
                    }

                    // If this profile item is new, persist it (will also generate all profile item values)
                    if (dataItem != null) {
                        profileItemService.persist(profileItem);
                    }

                    // Aggregate the results
                    String location = "/" + requestWrapper.getVersion() + "/profiles/" +
                        requestWrapper.getAttributes().get("profileIdentifier") + "/items/" + profileItem.getUid();
                    results.put(profileItem.getUid(), location);
                }
            }
            return ResponseHelper.getBatchProfileOK(requestWrapper, results);

        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected ProfileItemResource.ProfileItemValidator getValidator(RequestWrapper requestWrapper) {
        return (ProfileItemResource.ProfileItemValidator) resourceBeanFinder.getValidator(
            ProfileItemResource.ProfileItemValidator.class, requestWrapper);
    }
}
