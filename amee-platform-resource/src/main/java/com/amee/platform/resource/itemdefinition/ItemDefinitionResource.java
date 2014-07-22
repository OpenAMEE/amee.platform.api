package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceValidator;

public interface ItemDefinitionResource {

    interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

        public ItemDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        public void newItemDefinition(ItemDefinition itemDefinition);

        public void addBasic();

        public void addAudit();

        public void addName();

        public void addDrillDown();

        public void addUsages();

        public void addAlgorithms();
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface Remover extends ResourceRemover {
    }

    interface ItemDefinitionValidator extends ResourceValidator<ItemDefinition> {
    }
}