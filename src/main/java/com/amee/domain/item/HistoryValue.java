package com.amee.domain.item;

import com.amee.platform.science.ExternalHistoryValue;

import java.util.Date;

public interface HistoryValue extends ExternalHistoryValue {

    public void setStartDate(Date startDate);
}
