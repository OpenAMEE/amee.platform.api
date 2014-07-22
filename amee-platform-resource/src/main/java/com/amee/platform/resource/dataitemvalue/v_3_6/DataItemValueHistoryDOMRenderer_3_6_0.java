package com.amee.platform.resource.dataitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitemvalue.DataItemValueDOMRenderer;
import com.amee.platform.resource.dataitemvalue.DataItemValueHistoryResource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Renders a DataItemValue element that has been requested as part of a historical series, and therefore should have the <code>history</code> attribute omitted
 * (that attribute indicates that a history is available for the DataItemValue, and does not apply when displaying the set of items which make up that history).
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemValueHistoryDOMRenderer_3_6_0 extends DataItemValueDOMRenderer implements DataItemValueHistoryResource.Renderer {

    @Override
    public boolean isHistoryItem() {
        return true;
    }
}