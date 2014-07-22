package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.science.*;
import com.amee.service.auth.ResourceAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemBuilder_3_6_0 implements ProfileItemResource.Builder {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private ProfileItemResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get resource entities for this request
        Profile profile = resourceService.getProfile(requestWrapper);
        ProfileItem profileItem = resourceService.getProfileItem(requestWrapper, profile);

        // Authorised for profile item?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), profileItem);

        // Handle the profile item
        handle(requestWrapper, profileItem);
        ProfileItemResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    @Override
    public void handle(RequestWrapper requestWrapper, ProfileItem profileItem) {

        // Get renderer
        ProfileItemResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Get rendering options from matrix parameters
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean audit = requestWrapper.getMatrixParameters().containsKey("audit");
        boolean amounts = requestWrapper.getMatrixParameters().containsKey("amounts");
        boolean name = requestWrapper.getMatrixParameters().containsKey("name");
        boolean dates = requestWrapper.getMatrixParameters().containsKey("dates");
        boolean category = requestWrapper.getMatrixParameters().containsKey("category");
        boolean note = requestWrapper.getMatrixParameters().containsKey("note");

        // New Profile Item and basic
        renderer.newProfileItem(profileItem);
        renderer.addBasic();

        // Optionals
        if (audit || full) {
            renderer.addAudit();
        }
        if (name || full) {
            renderer.addName();
        }
        if (dates || full) {
            TimeZone currentUserTimeZone = resourceService.getCurrentUser(requestWrapper).getTimeZone();
            renderer.addDates(currentUserTimeZone);
        }
        if (category || full) {
            renderer.addCategory();
        }
        if (amounts || full) {
            ReturnValues returnValues = profileItem.getAmounts();

            // Don't bother trying to convert if we have no custom units or results (eg algorithm error)
            Choices returnUnitChoices = getReturnUnitParameters(requestWrapper);
            if (returnUnitChoices.getChoices().isEmpty() || !returnValues.hasReturnValues()) {
                renderer.addReturnValues(returnValues);
            } else {

                // Convert
                ReturnValues convertedReturnValues = new ReturnValues();
                for (Map.Entry<String, ReturnValue> entry: returnValues.getReturnValues().entrySet()) {
                    String type = entry.getKey();
                    ReturnValue returnValue = entry.getValue();

                    String unit = returnUnitChoices.get("returnUnits." + type) != null ?
                        returnUnitChoices.get("returnUnits." + type).getValue() : "";
                    String perUnit = returnUnitChoices.get("returnPerUnits." + type) != null ?
                        returnUnitChoices.get("returnPerUnits." + type).getValue() : "";

                    CO2AmountUnit returnUnit = new CO2AmountUnit(unit, perUnit);
                    Amount amount = returnValue.toAmount();
                    Amount convertedAmount = amount.convert(returnUnit);
                    convertedReturnValues.putValue(returnValue.getType(), returnUnit.getUnit().toString(),
                        returnUnit.getPerUnit().toString(), convertedAmount.getValue());
                }

                // Set the default type (an algorithm error may cause this to be null).
                convertedReturnValues.setDefaultType(returnValues.getDefaultType());

                // Copy the notes over
                for (Note returnValueNote: returnValues.getNotes()) {
                    convertedReturnValues.addNote(returnValueNote.getType(), returnValueNote.getValue());
                }

                // Render the ReturnValues.
                renderer.addReturnValues(convertedReturnValues);
            }
        }

        if (note || full) {
            renderer.addNote();
        }
    }

    @Override
    public ProfileItemResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer =
                    (ProfileItemResource.Renderer) resourceBeanFinder.getRenderer(ProfileItemResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }

    private Choices getReturnUnitParameters(RequestWrapper requestWrapper) {
        List<Choice> parameterChoices = new ArrayList<Choice>();

        // Get the map of all query parameters.
        Map<String, String> queryParameters = new HashMap<String, String>(requestWrapper.getQueryParameters());
        for (String name : queryParameters.keySet()) {

            // Only add returnUnits, returnPerUnits
            if (name.startsWith("returnUnits.") || name.startsWith("returnPerUnits.")) {
                parameterChoices.add(new Choice(name, requestWrapper.getQueryParameters().get(name)));
            }
        }
        return new Choices("returnUnits", parameterChoices);
    }
}
