package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.resource.dataitem.v_3_1.DataItemDOMRenderer_3_1_0;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Version 3.0 does not include the 'history' flag of an ItemValue. Otherwise the representation is the same as 3.1.
 */
@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemDOMRenderer_3_0_0 extends DataItemDOMRenderer_3_1_0 {

    @Override
    public void newValue(BaseItemValue itemValue) {
        Element valueElem = new Element("Value");
        valueElem.addContent(new Element("Path").setText(itemValue.getPath()));
        valueElem.addContent(new Element("Value").setText(itemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
            NumberValue nv = (NumberValue) itemValue;
            if (nv.hasUnit()) {
                valueElem.addContent(new Element("Unit").setText(nv.getUnit().toString()));
                if (nv.hasPerUnit()) {
                    valueElem.addContent(new Element("PerUnit").setText(nv.getPerUnit().toString()));
                    valueElem.addContent(new Element("CompoundUnit").setText(nv.getCompoundUnit().toString()));
                }
            }
        }
        valuesElem.addContent(valueElem);
    }
}
