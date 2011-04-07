package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.unit.AMEEUnit;
import com.amee.platform.resource.unit.UnitResource;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitJSONRenderer_3_5_0 implements UnitResource.Renderer {

    protected AMEEUnit unit;
    protected JSONObject rootObj;
    protected JSONObject unitObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newUnit(AMEEUnit unit) {
        this.unit = unit;
        unitObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "unit", unitObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(unitObj, "uid", unit.getUid());
        ResponseHelper.put(unitObj, "name", unit.getName());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(unitObj, "status", unit.getStatus().getName());
        ResponseHelper.put(unitObj, "created", DATE_FORMAT.print(unit.getCreated().getTime()));
        ResponseHelper.put(unitObj, "modified", DATE_FORMAT.print(unit.getModified().getTime()));
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
