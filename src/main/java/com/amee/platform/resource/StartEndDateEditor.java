package com.amee.platform.resource;

import com.amee.domain.IDataItemService;
import com.amee.platform.science.StartEndDate;

import java.beans.PropertyEditorSupport;
import java.util.Date;

public class StartEndDateEditor extends PropertyEditorSupport {

    private Date defaultDate = null;

    private StartEndDateEditor() {
        super();
    }

    public StartEndDateEditor(Date defaultDate) {
        this();
        this.defaultDate = defaultDate;
    }

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            if (text.equals("CURRENT")) {
                setValue(new StartEndDate());
            } else if (text.equals("FIRST")) {
                setValue(new StartEndDate(IDataItemService.EPOCH));
            } else if (text.equals("LAST")) {
                setValue(new StartEndDate(IDataItemService.Y2038));
            } else {
                setValue(new StartEndDate(text));
            }
        } else {
            setValue(defaultDate);
        }
    }
}
