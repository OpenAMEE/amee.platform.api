package com.amee.platform.resource.drill.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.data.DataCategory;
import com.amee.domain.sheet.Choice;
import com.amee.domain.sheet.Choices;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.drill.DrillResource;
import com.amee.platform.search.SearchDrillDownService;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DrillBuilder_3_3_0 implements DrillResource.Builder {

    @Autowired
    private SearchDrillDownService searchDrillDownService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    private DrillResource.Renderer renderer;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Object handle(RequestWrapper requestWrapper) {

        // Get entities.
        DataCategory dataCategory = resourceService.getDataCategoryWhichHasItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForBuild(
                requestWrapper.getAttributes().get("activeUserUid"), dataCategory);

        // Handle the DataCategory.
        this.handle(requestWrapper, dataCategory);
        DrillResource.Renderer renderer = getRenderer(requestWrapper);
        renderer.ok();
        return renderer.getObject();
    }

    protected void handle(RequestWrapper requestWrapper, DataCategory dataCategory) {

        List<Choice> selections = getSelections(requestWrapper);
        Choices choices = searchDrillDownService.getChoices(dataCategory, selections);

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
