package com.amee.platform.resource.drill.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.domain.data.DataCategory;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.drill.DrillResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import com.amee.service.data.DrillDownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DrillBuilder_3_3_0 implements DrillResource.Builder {

    @Autowired
    private DataService dataService;

    @Autowired
    private DrillDownService drillDownService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    private DrillResource.Renderer renderer;

    @Override
    public Object handle(RequestWrapper requestWrapper) {
        String dataCategoryIdentifier = requestWrapper.getAttributes().get("categoryIdentifier");
        if (dataCategoryIdentifier != null) {
            DataCategory dataCategory = dataService.getDataCategoryByIdentifier(dataCategoryIdentifier);
            if ((dataCategory != null) && dataCategory.isItemDefinitionPresent()) {
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForBuild(
                        requestWrapper.getAttributes().get("activeUserUid"), dataCategory);
                // Handle the DataCategory.
                this.handle(requestWrapper, dataCategory);
                DrillResource.Renderer renderer = getRenderer(requestWrapper);
                renderer.ok();
                return renderer.getObject();
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("dataCategoryIdentifier");
        }
    }

    protected void handle(RequestWrapper requestWrapper, DataCategory dataCategory) {

        List<Choice> selections = getSelections(requestWrapper);
        Choices choices = drillDownService.getChoices(dataCategory, selections);

        DrillResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.start();

        // Selections.
        renderer.startSelections();
        for (Choice selection : selections) {
            renderer.newSelection(selection);
        }

        // Choices.
        renderer.startChoices(choices.getName());
        for (Choice choice : choices.getChoices()) {
            renderer.newChoice(choice);
        }
    }

    protected List<Choice> getSelections(RequestWrapper requestWrapper) {
        List<Choice> selections = new ArrayList<Choice>();
        for (String name : requestWrapper.getQueryParameters().keySet()) {
            selections.add(new Choice(name, requestWrapper.getQueryParameters().get(name)));
        }
        return selections;
    }

    protected DrillResource.Renderer getRenderer(RequestWrapper requestWrapper) {
        if (renderer == null) {
            renderer = (DrillResource.Renderer) resourceBeanFinder.getRenderer(DrillResource.Renderer.class, requestWrapper);
        }
        return renderer;
    }
}
