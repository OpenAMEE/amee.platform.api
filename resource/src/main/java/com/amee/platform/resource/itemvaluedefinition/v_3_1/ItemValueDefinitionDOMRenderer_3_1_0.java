package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.domain.ValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.v_3_4.ItemValueDefinitionDOMRenderer_3_4_0;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionDOMRenderer_3_1_0 extends ItemValueDefinitionDOMRenderer_3_4_0 {

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        Element e = new Element("ValueDefinition");
        itemValueDefinitionElem.addContent(e);
        e.setAttribute("uid", valueDefinition.getUid());
        e.addContent(new Element("Name").setText(valueDefinition.getName()));
        e.addContent(new Element("ValueType").setText(valueDefinition.getValueType().getName()));
    }
}
