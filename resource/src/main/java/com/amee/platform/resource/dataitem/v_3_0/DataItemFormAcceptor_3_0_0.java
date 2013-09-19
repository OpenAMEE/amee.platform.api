package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.v_3_4.DataItemFormAcceptor_3_4_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Extends DataItemFormAcceptor_3_4_0 so that the updateDataItemValues has an empty implementation.
 */
@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemFormAcceptor_3_0_0 extends DataItemFormAcceptor_3_4_0 {

    protected void updateDataItemValues(DataItem dataItem) {
        // Not in 3.0.
    }
}
