package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.calculation.service.CalculationService;
import com.amee.domain.APIVersion;
import com.amee.domain.DataItemService;
import com.amee.domain.data.BaseItemValueStartDateComparator;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.UsableValuePredicate;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.dataitem.DataItemCalculationResource;
import com.amee.platform.science.ExternalHistoryValue;
import com.amee.platform.science.ReturnValues;
import com.amee.platform.science.StartEndDate;
import com.amee.service.auth.ResourceAuthorizationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemCalculationBuilder_3_4_0 implements DataItemCalculationResource.Builder {

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

    private final Log log = LogFactory.getLog(getClass());

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
        if (StringUtils.isNotBlank(startDate)) {
            dataItem.setEffectiveStartDate(new StartEndDate(startDate));
        }

        String endDate = requestWrapper.getQueryParameters().get("endDate");
        if (StringUtils.isNotBlank(endDate)) {
            dataItem.setEffectiveEndDate(new StartEndDate(endDate));
        }

        // Get all the parameters.
        List<Choice> parameters = getParameters(requestWrapper);

        // Get the available user choices (with any defaults set).
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

            // Get the data item values
            Map<String, List<BaseItemValue>> dataItemValues =
                getDataItemValues(dataItem, new StartEndDate(startDate), new StartEndDate(endDate));
            
            renderer.addValues(userValueChoices, dataItemValues);
        }
    }

    /**
     * Creates a List of Choices from the submitted parameters. Only values.*, units.* and perUnits.* values are used.
     *
     * Values parameters will have their 'values.' prefix stripped.
     *
     * @param requestWrapper
     * @return
     */
    private List<Choice> getParameters(RequestWrapper requestWrapper) {

        // Get the map of query parameters but remove special parameters values.
        Map<String, String> queryParameters = new HashMap<String, String>(requestWrapper.getQueryParameters());
        // queryParameters.remove("returnUnit");
        // queryParameters.remove("returnPerUnit");
        queryParameters.remove("startDate");
        queryParameters.remove("endDate");

        // Create list of Choices for parameters.
        List<Choice> parameterChoices = new ArrayList<Choice>();
        for (String name : queryParameters.keySet()) {
            String value = requestWrapper.getQueryParameters().get(name);

            // Only add those parameters we are expecting (values, units, perUnits)
            if (name.startsWith("values.")) {
                parameterChoices.add(new Choice(StringUtils.removeStart(name, "values."), value));
            } else if (name.startsWith("units.") || name.startsWith("perUnits.")) {
                parameterChoices.add(new Choice(name, value));
            }
        }
        return parameterChoices;
    }

    @Override
    public DataItemCalculationResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DataItemCalculationResource.Renderer) resourceBeanFinder.getRenderer(DataItemCalculationResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
    
    private Map<String, List<BaseItemValue>> getDataItemValues(DataItem dataItem, Date startDate, Date endDate) {
        Map<String, List<BaseItemValue>> dataItemValues = new HashMap<String, List<BaseItemValue>>();
        Map<String, ItemValueDefinition> itemValueDefinitions = dataItem.getItemDefinition().getItemValueDefinitionsMap();
        for (Map.Entry<String, ItemValueDefinition> entry: itemValueDefinitions.entrySet()) {
            String path = entry.getKey();
            ItemValueDefinition itemValueDefinition = entry.getValue();

            if (itemValueDefinition.isFromData()) {

                // Get all ItemValues with this ItemValueDefinition path.
                List<BaseItemValue> itemValues = dataItemService.getAllItemValues(dataItem, path);

                // Add all BaseItemValues with usable values
                List<BaseItemValue> usableSet = (List<BaseItemValue>) CollectionUtils.select(itemValues, new UsableValuePredicate());

                // Filter the values for the date range.
                List<BaseItemValue> filteredValues = filterItemValues(usableSet, startDate, endDate);

                dataItemValues.put(path, filteredValues);
            }
        }
        return dataItemValues;
    }

    // TODO: Duplication com.amee.platform.science.InternalValue.filterItemValues()
    /**
     * Filter the ItemValue collection by the effective start and end dates of the owning Item.
     * ItemValues are excluded if they start prior to startDate and are not the final value in the sequence.
     * ItemValues are excluded if they start on or after the endDate.
     * The item value immediately prior to the start of the selection interval should be kept
     *
     * @param values    ItemValues to filter
     * @param startDate effective startDate of Item
     * @param endDate   effective endDate of Item
     * @return the filtered values
     */
    private List<BaseItemValue> filterItemValues(List<BaseItemValue> values, Date startDate, Date endDate) {
        List<BaseItemValue> filteredValues = new ArrayList<BaseItemValue>();

        // sort in descending order (most recent last, non-historical value first)
        Collections.sort(values, new BaseItemValueStartDateComparator());

        // endDate can be nil, indicating range-of-interest extends to infinite future time
        // in this case, only the final value in the interval is of interest to anyone
        if (endDate == null) {
            filteredValues.add(values.get(values.size() - 1));
            return filteredValues;
        }

        // The earliest value
        BaseItemValue previous = values.get(0);
        StartEndDate latest;
        if (BaseItemValueStartDateComparator.isHistoricValue(previous)) {
            latest = ((ExternalHistoryValue)previous).getStartDate();
        } else {

            // Set the epoch.
            latest = new StartEndDate(new Date(0));
        }

        for (BaseItemValue iv : values) {
            StartEndDate currentStart;
            if (BaseItemValueStartDateComparator.isHistoricValue(iv)) {
                currentStart = ((ExternalHistoryValue)iv).getStartDate();
            } else {
                currentStart = new StartEndDate(new Date(0));
            }

            if (currentStart.before(endDate) && !currentStart.before(startDate)) {
                filteredValues.add(iv);
            } else if (currentStart.before(startDate) && currentStart.after(latest)) {
                latest = currentStart;
                previous = iv;
            }
        }

        // Add the previous point to the start of the list
        if (BaseItemValueStartDateComparator.isHistoricValue(previous)) {
            log.info("Adding previous point at " + ((ExternalHistoryValue)previous).getStartDate());
        } else {
            log.info("Adding previous point at " + new StartEndDate(new Date(0)));
        }
        filteredValues.add(0, previous);

        return filteredValues;
    }
}