/**
 * This file is part of AMEE.
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.calculation.service;

import com.amee.base.transaction.TransactionController;
import com.amee.domain.AMEEStatistics;
import com.amee.domain.APIVersion;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.profile.CO2CalculationService;
import com.amee.domain.profile.ProfileItem;
import com.amee.domain.sheet.Choices;
import com.amee.platform.science.AlgorithmRunner;
import com.amee.platform.science.CO2Amount;
import com.amee.platform.science.InternalValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.org.mozilla.javascript.internal.JavaScriptException;

import javax.script.ScriptException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
public class CalculationService implements CO2CalculationService, BeanFactoryAware, Serializable {

    private final Log log = LogFactory.getLog(getClass());
    private final Log scienceLog = LogFactory.getLog("science");

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private AMEEStatistics ameeStatistics;

    private AlgorithmRunner algorithmRunner = new AlgorithmRunner();

    // Set by Spring context. The BeanFactory used to retrieve ProfileFinder and DataFinder instances.
    private BeanFactory beanFactory;

    /**
     * Calculate and always set the CO2 amount for a ProfileItem.
     *
     * @param profileItem - the ProfileItem for which to calculate CO2 amount
     */
    public void calculate(ProfileItem profileItem) {

        CO2Amount amount = CO2Amount.ZERO;

        // End marker ProfileItems can only have zero amounts.
        if (!profileItem.isEnd()) {
            // Calculate amount for ProfileItem if an Algorithm is available.
            // Some ProfileItems are from ItemDefinitions which do not have Algorithms and
            // hence do not support calculations.
            if (profileItem.supportsCalculation()) {
                Algorithm algorithm = profileItem.getItemDefinition().getAlgorithm(Algorithm.DEFAULT);
                if (algorithm != null) {
                    Map<String, Object> values = getValues(profileItem);
                    amount = calculate(algorithm, values);
                }
            }
        }

        // Always set the ProfileItem amount.
        profileItem.setAmount(amount);
    }

    /**
     * Calculate and return the CO2 amount for a DataItem and a set of user specified values.
     * <p/>
     * Note: I am unsure if this is in active use (SM)
     *
     * @param dataItem         - the DataItem for the calculation
     * @param userValueChoices - user supplied value choices
     * @param version          - the APIVersion. This is used to determine the correct ItemValueDefinitions to load into the calculation
     * @return the calculated CO2 amount
     */
    public CO2Amount calculate(DataItem dataItem, Choices userValueChoices, APIVersion version) {
        CO2Amount amount = CO2Amount.ZERO;
        Algorithm algorithm = dataItem.getItemDefinition().getAlgorithm(Algorithm.DEFAULT);
        if (algorithm != null) {
            Map<String, Object> values = getValues(dataItem, userValueChoices, version);
            amount = calculate(algorithm, values);
        }
        return amount;
    }

    /**
     * Calculate and return the CO2 amount for given the provided algorithm and input values.
     * <p/>
     * Intended to be used publically in test harnesses when passing the modified algorithm content and input values
     * for execution is desirable.
     *
     * @param algorithm the algorithm to use
     * @param values    input values for the algorithm
     * @return the algorithm result
     */
    public CO2Amount calculate(Algorithm algorithm, Map<String, Object> values) {

        if (log.isDebugEnabled()) {
            log.debug("calculate()");
            log.debug("calculate() - algorithm uid: " + algorithm.getUid());
            log.debug("calculate() - input values: " + values);
            log.debug("calculate() - starting calculation");
        }

        CO2Amount amount;
        final long startTime = System.nanoTime();

        try {
            amount = new CO2Amount(algorithmRunner.evaluate(algorithm, values));
        } catch (ScriptException e) {

            // Bubble up parameter missing or format exceptions from the
            // algorithms (the only place where these validations can be performed.
            IllegalArgumentException iae = AlgorithmRunner.getIllegalArgumentException(e);
            if (iae != null) {
                throw iae;
            }

            // Throw CalculationException for Exceptions from the JavaScript 'throw' keyword.
            if ((e.getCause() != null) && e.getCause() instanceof JavaScriptException) {
                JavaScriptException jse = (JavaScriptException) e.getCause();
                throw new CalculationException(
                        "Caught Exception in Algorithm (" +
                                algorithm.getItemDefinition().getName() +
                                ", " +
                                algorithm.getName() +
                                ", " +
                                jse.lineNumber() +
                                ", " +
                                jse.columnNumber() +
                                "): " + jse.getValue());

            }

            // Log all other errors to the science log...
            scienceLog.warn(
                    "Caught ScriptException in Algorithm (" +
                            algorithm.getItemDefinition().getName() +
                            ", " +
                            algorithm.getName() +
                            "): " + e.getMessage());

            // ...and return zero by default.
            amount = CO2Amount.ZERO;
        } finally {
            ameeStatistics.addToThreadCalculationDuration(System.nanoTime() - startTime);
        }

        if (log.isDebugEnabled()) {
            log.debug("calculate() - finished calculation");
            log.debug("calculate() - CO2 Amount: " + amount);
        }

        return amount;
    }

    // Collect all relevant algorithm input values for a ProfileItem calculation.

    private Map<String, Object> getValues(ProfileItem profileItem) {

        Map<ItemValueDefinition, InternalValue> values = new HashMap<ItemValueDefinition, InternalValue>();
        Map<String, Object> returnValues = new HashMap<String, Object>();

        // Add ItemDefinition defaults.
        APIVersion apiVersion = profileItem.getProfile().getUser().getAPIVersion();
        profileItem.getItemDefinition().appendInternalValues(values, apiVersion);

        // Add DataItem values, filtered by start and end dates of the ProfileItem (factoring in the query date range).
        DataItem dataItem = profileItem.getDataItem();
        dataItem.setEffectiveStartDate(profileItem.getEffectiveStartDate());
        dataItem.setEffectiveEndDate(profileItem.getEffectiveEndDate());
        dataItem.appendInternalValues(values);

        // Add the ProfileItem values.
        profileItem.appendInternalValues(values);

        // Add actual values to returnValues list based on InternalValues in values list.
        for (Map.Entry<ItemValueDefinition, InternalValue> entry : values.entrySet()) {
            returnValues.put(entry.getKey().getCanonicalPath(), entry.getValue().getValue());
        }

        // Initialise finders for algorithm.
        initFinders(profileItem, returnValues);

        return returnValues;
    }

    /**
     * Add DataFinder, ProfileFinder and ServiceFinder to the algorithm values.
     *
     * @param profileItem to be used in finders
     * @param values      to place finders into
     */
    private void initFinders(ProfileItem profileItem, Map<String, Object> values) {

        // Configure and add DataFinder.
        DataFinder dataFinder = (DataFinder) beanFactory.getBean("dataFinder");
        dataFinder.setEnvironment(profileItem.getEnvironment());
        dataFinder.setStartDate(profileItem.getStartDate());
        dataFinder.setEndDate(profileItem.getEndDate());
        values.put("dataFinder", dataFinder);

        // Configure and add ProfileFinder.
        ProfileFinder profileFinder = (ProfileFinder) beanFactory.getBean("profileFinder");
        profileFinder.setProfileItem(profileItem);
        profileFinder.setDataFinder(dataFinder);
        values.put("profileFinder", profileFinder);

        // Configure and add ServiceFinder.
        ServiceFinder serviceFinder = (ServiceFinder) beanFactory.getBean("serviceFinder");
        serviceFinder.setValues(values);
        serviceFinder.setProfileFinder(profileFinder);
        values.put("serviceFinder", serviceFinder);
    }

    // Collect all relevant algorithm input values for a DataItem + auth Choices calculation.

    private Map<String, Object> getValues(DataItem dataItem, Choices userValueChoices, APIVersion version) {

        Map<ItemValueDefinition, InternalValue> values = new HashMap<ItemValueDefinition, InternalValue>();
        dataItem.getItemDefinition().appendInternalValues(values, version);
        dataItem.appendInternalValues(values);
        appendUserValueChoices(dataItem.getItemDefinition(), userValueChoices, values, version);

        Map<String, Object> returnValues = new HashMap<String, Object>();
        for (Map.Entry<ItemValueDefinition, InternalValue> entry : values.entrySet()) {
            returnValues.put(entry.getKey().getCanonicalPath(), entry.getValue().getValue());
        }

        DataFinder dataFinder = (DataFinder) beanFactory.getBean("dataFinder");
        dataFinder.setEnvironment(dataItem.getEnvironment());

        ProfileFinder profileFinder = (ProfileFinder) beanFactory.getBean("profileFinder");
        profileFinder.setDataFinder(dataFinder);

        ServiceFinder serviceFinder = (ServiceFinder) beanFactory.getBean("serviceFinder");
        serviceFinder.setValues(returnValues);
        serviceFinder.setProfileFinder(profileFinder);

        returnValues.put("serviceFinder", serviceFinder);
        returnValues.put("dataFinder", dataFinder);
        returnValues.put("profileFinder", profileFinder);

        return returnValues;
    }

    private void appendUserValueChoices(
            ItemDefinition itemDefinition,
            Choices userValueChoices,
            Map<ItemValueDefinition, InternalValue> values,
            APIVersion version) {
        if (userValueChoices != null) {
            Map<ItemValueDefinition, InternalValue> userChoices = new HashMap<ItemValueDefinition, InternalValue>();
            for (ItemValueDefinition itemValueDefinition : itemDefinition.getItemValueDefinitions()) {
                // Add each submitted user Choice that is available in the ItemDefinition and for the user's APIVersion
                if (itemValueDefinition.isFromProfile() &&
                        userValueChoices.containsKey(itemValueDefinition.getPath()) &&
                        itemValueDefinition.isValidInAPIVersion(version)) {
                    // Create transient ItemValue.
                    ItemValue itemValue = new ItemValue();
                    itemValue.setItemValueDefinition(itemValueDefinition);
                    itemValue.setValue(userValueChoices.get(itemValueDefinition.getPath()).getValue());
                    if (version.isNotVersionOne()) {
                        if (itemValue.hasUnit() && userValueChoices.containsKey(itemValueDefinition.getPath() + "Unit")) {
                            itemValue.setUnit(userValueChoices.get(itemValueDefinition.getPath() + "Unit").getValue());
                        }
                        if (itemValue.hasPerUnit() && userValueChoices.containsKey(itemValueDefinition.getPath() + "PerUnit")) {
                            itemValue.setPerUnit(userValueChoices.get(itemValueDefinition.getPath() + "PerUnit").getValue());
                        }
                    }
                    // Only add ItemValue value if it is usable.
                    if (itemValue.isUsableValue()) {
                        userChoices.put(itemValueDefinition, new InternalValue(itemValue));
                    }
                }
            }
            values.putAll(userChoices);
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}