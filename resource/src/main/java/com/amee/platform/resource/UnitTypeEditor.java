package com.amee.platform.resource;

import com.amee.service.unit.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Custom editor to convert an identifier (UID or name) into an Unit Type.
 */
@Component
public class UnitTypeEditor extends PropertyEditorSupport {

    @Autowired
    private UnitService unitService;

    @Override
    public void setAsText(String text) {
        if (text != null) {
            setValue(unitService.getUnitTypeByIdentifier(text));
        }
    }
}
