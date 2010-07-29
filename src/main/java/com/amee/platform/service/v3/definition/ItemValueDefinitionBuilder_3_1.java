package com.amee.platform.service.v3.definition;

import com.amee.base.domain.Since;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionBuilder_3_1 extends ItemValueDefinitionBuilder {

    private final static Map<String, Class> RENDERERS = new HashMap<String, Class>() {
        {
            put("application/json", ItemValueDefinitionJSONRenderer_3_1.class);
            put("application/xml", ItemValueDefinitionDOMRenderer_3_1.class);
        }
    };

    public Map<String, Class> getRenderers() {
        return RENDERERS;
    }
}