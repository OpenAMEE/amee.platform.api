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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Receives and processes batch updates to Profile Items submitted in XML format.
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsDOMAcceptor_3_6_0 implements ProfileItemsResource.DOMAcceptor {

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

        // Get profile
        Profile profile = resourceService.getProfile(requestWrapper);

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(requestWrapper.getAttributes().get("activeUserUid"), profile);

        // Get request body
        Document requestBodyDocument = requestWrapper.getBodyAsDocument();
        Element rootElement = requestBodyDocument.getRootElement();

        if (rootElement.getName().equalsIgnoreCase("ProfileCategory")) {
            Element profileItemsElement = rootElement.getChild("ProfileItems");

            List<Element> profileItemElements = profileItemsElement.getChildren();
            for (Element profileItemElement : profileItemElements) {

                DataItem dataItem = null;
                ProfileItem profileItem = null;
                String name, value;
                Map<String, String> parameters = new HashMap<String, String>();

                List<Element> parameterElements = profileItemElement.getChildren();
                for (Element parameterElement : parameterElements) {

                    name = parameterElement.getName();
                    value = parameterElement.getText();

                    if ("dataItemUid".equalsIgnoreCase(name)) {
                        dataItem = dataItemService.getItemByUid(value);

                    } else if ("category".equalsIgnoreCase(name)) {
                        DataCategory dataCategory = dataService.getDataCategoryByIdentifier(value);
                        dataItem = resourceService.getDataItem(requestWrapper, dataCategory);

                    } else if ("profileItemUid".equalsIgnoreCase(name)) {
                        profileItem = profileItemService.getItemByUid(value);

                    } else {
                        parameters.put(name, value);
                    }
                }

                // Make a new profile item if a data item was specified in the request
                if (dataItem != null) {
                    profileItem = new ProfileItem(profile, dataItem);
                }

                // Check we were able to find an existing profile item or create a new one from the specified data item
                if (profileItem == null) {
                    throw new NotFoundException();
                }

                ProfileItemResource.ProfileItemValidator validator = getValidator(requestWrapper);
                validator.setObject(profileItem);

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
                entities.put(profileItem.getUid(), location);
            }
        }

        return ResponseHelper.getBatchProfileOK(requestWrapper, entities);
    }

    protected ProfileItemResource.ProfileItemValidator getValidator(RequestWrapper requestWrapper) {
        return (ProfileItemResource.ProfileItemValidator) resourceBeanFinder.getValidator(
            ProfileItemResource.ProfileItemValidator.class, requestWrapper);
    }
}
