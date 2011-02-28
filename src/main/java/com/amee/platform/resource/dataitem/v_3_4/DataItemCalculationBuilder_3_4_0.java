package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.calculation.service.CalculationService;
import com.amee.domain.APIVersion;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.ReturnValues;
import com.amee.platform.science.StartEndDate;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.item.DataItemService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Specify units and perUnits.
 * TODO: Validation of each parameter against matching IVDs.
 */
@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemCalculationBuilder_3_4_0 implements DataItemCalculationResource.Builder {

    @Autowired
    private DataService dataService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private DataItemService dataItemService;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DataItemCalculationResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {
        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);
        DataItem dataItem = resourceService.getDataItem(requestWrapper, dataCategory);
        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataItem);
        // Handle the DataItem.
        this.handle(requestWrapper, dataItem);
        DataItemCalculationResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    public void handle(RequestWrapper requestWrapper, DataItem dataItem) {

        // Matrix parameters.
        boolean full = requestWrapper.getMatrixParameters().containsKey("full");
        boolean values = requestWrapper.getMatrixParameters().containsKey("values");

        // Start the Renderer.
        DataItemCalculationResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Get special parameters.
        // String unit = requestWrapper.getQueryParameters().get("returnUnit");
        // String perUnit = requestWrapper.getQueryParameters().get("returnPerUnit");
        String startDate = requestWrapper.getQueryParameters().get("startDate");

        // The resource may receive a startDate parameter that sets the current date in an
        // historical sequence of ItemValues.
        if (StringUtils.isNotBlank(startDate)) {
            dataItem.setEffectiveStartDate(new StartEndDate(startDate));
        }

        // Get all the parameters.
        List<Choice> parameters = getParameters(requestWrapper);

        // Prepare the value choices.
        // TODO: Is hard-coding the APIVersion OK?
        Choices userValueChoices = dataItemService.getUserValueChoices(dataItem, APIVersion.TWO);
        userValueChoices.merge(parameters);

        // Do the calculation.
        ReturnValues returnValues = calculationService.calculate(dataItem, userValueChoices, APIVersion.TWO);

        // Render the ReturnValues.
        renderer.addDataItem(dataItem);
        renderer.addReturnValues(returnValues);

        // Render the values.
        if (values || full) {
            renderer.addValues(userValueChoices);
        }
    }

    private List<Choice> getParameters(RequestWrapper requestWrapper) {
        // Get the map of query parameters but remove special parameters values.
        Map<String, String> queryParameters = new HashMap<String, String>(requestWrapper.getQueryParameters());
        // queryParameters.remove("returnUnit");
        // queryParameters.remove("returnPerUnit");
        queryParameters.remove("startDate");
        // Create list of Choices for parameters.
        List<Choice> parameterChoices = new ArrayList<Choice>();
        for (String name : queryParameters.keySet()) {
            parameterChoices.add(new Choice(name, requestWrapper.getQueryParameters().get(name)));
        }
        return parameterChoices;
    }

    public DataItemCalculationResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemCalculationResource.Renderer) resourceBeanFinder.getRenderer(DataItemCalculationResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}