package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitemvalue.DataItemValueDOMRenderer;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueDOMRenderer_3_4_0 extends DataItemValueDOMRenderer implements DataItemValueResource.Renderer {

    @Override
    public boolean isHistoryItem() {
        return false;
    }
}
