package com.amee.platform.resource.returnvaluedefinition.v_3_2;

import com.amee.base.domain.Since;
import com.amee.domain.ValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.v_3_4.ReturnValueDefinitionDOMRenderer_3_4_0;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class ReturnValueDefinitionDOMRenderer_3_2_0 extends ReturnValueDefinitionDOMRenderer_3_4_0 {

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        Element e = new Element("ValueDefinition");
        returnValueDefinitionElem.addContent(e);
        e.setAttribute("uid", valueDefinition.getUid());
        e.addContent(new Element("Name").setText(valueDefinition.getName()));
        e.addContent(new Element("ValueType").setText(valueDefinition.getValueType().getName()));
    }
}
