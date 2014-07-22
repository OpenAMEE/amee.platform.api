package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.platform.resource.itemdefinition.v_3_4.ItemDefinitionDOMRenderer_3_4_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionDOMRenderer_3_1_0 extends ItemDefinitionDOMRenderer_3_4_0 {

    @Override
    public void addAlgorithms() {
        // Not in 3.1.
    }
}
