package com.amee.platform.resource.dataitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.domain.DataItemService;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;
import com.amee.platform.resource.dataitemvalue.v_3_4.DataItemValueDOMRenderer_3_4_0;

import org.jdom.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Renders a DataItemValue element that has been requested as part of a historical series, and therefore should have the <code>history</code> attribute omitted
 * (that attribute indicates that a history is available for the DataItemValue, and does not apply when displaying the set
 * of items which make up that history).
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemValueHistoryDOMRenderer_3_6_0 extends DataItemValueDOMRenderer_3_4_0 implements DataItemValueHistoryResource.Renderer {

    /**
     * This method implemenation is identical to {@link DataItemValueDOMRenderer_3_4_0#addBasic()} with the
     * exception that it does not include a <code>history</code> attribute on the <code><dataItemValue></code> element.
     */
    @Override
    public void addBasic() {
        dataItemValueElem.setAttribute("uid", dataItemValue.getUid());
        dataItemValueElem.addContent(new Element("Value").setText(dataItemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(dataItemValue.getClass())) {
            NumberValue nv = (NumberValue) dataItemValue;
            if (nv.hasUnit()) {
                dataItemValueElem.addContent(new Element("Unit").setText(nv.getCompoundUnit().toString()));
            }
        }
        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            HistoryValue hv = (HistoryValue) dataItemValue;
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(hv.getStartDate().getTime())));
        } else {
            dataItemValueElem.addContent(new Element("StartDate").setText(DATE_FORMAT.print(DataItemService.MYSQL_MIN_DATETIME.getTime())));
        }
    }
}
