package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitem.v_3_4.DataItemValidator_3_4_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * DataItem validation. Extends DataItemValidator_3_4_0 but does not support DataItem values.
 */
@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemValidator_3_0_0 extends DataItemValidator_3_4_0 {

    public DataItemValidator_3_0_0() {
        super();
    }

    @Override
    protected void addValues() {
        // Not in 3.0.
    }

    @Override
    protected void addUnits() {
        // Not in 3.0.
    }

    @Override
    protected void addPerUnits() {
        // Not in 3.0.
    }
}
