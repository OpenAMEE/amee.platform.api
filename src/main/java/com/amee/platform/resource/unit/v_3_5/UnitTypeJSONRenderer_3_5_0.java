package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.unit.UnitTypeResource;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeJSONRenderer_3_5_0 implements UnitTypeResource.Renderer {

    protected AMEEUnitType unitType;
    protected JSONObject rootObj;
    protected JSONObject unitTypeObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newUnitType(AMEEUnitType unitType) {
        this.unitType = unitType;
        unitTypeObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "value", unitTypeObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(unitTypeObj, "uid", unitType.getUid());
        ResponseHelper.put(unitTypeObj, "name", unitType.getName());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(unitTypeObj, "status", unitType.getStatus().getName());
        ResponseHelper.put(unitTypeObj, "created", DATE_FORMAT.print(unitType.getCreated().getTime()));
        ResponseHelper.put(unitTypeObj, "modified", DATE_FORMAT.print(unitType.getModified().getTime()));
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
