package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.science.ExternalHistoryValue;
import com.amee.platform.science.StartEndDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "DATA_ITEM_NUMBER_VALUE_HISTORY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemNumberValueHistory extends BaseDataItemNumberValue implements ExternalHistoryValue {

    @Column(name = "START_DATE")
    @Index(name = "START_DATE_IND")
    private Date startDate = new Date();

    public DataItemNumberValueHistory() {
        super();
    }

    public DataItemNumberValueHistory(ItemValueDefinition itemValueDefinition, NuDataItem dataItem, Double value) {
        super(itemValueDefinition, dataItem, value);
    }

    public DataItemNumberValueHistory(ItemValueDefinition itemValueDefinition, NuDataItem dataItem, Double value, Date startDate) {
        this(itemValueDefinition, dataItem, value);
        setStartDate(startDate);
    }

    protected void copyTo(DataItemNumberValueHistory o) {
        super.copyTo(o);
        o.startDate = startDate;
    }

    @Override
    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DINVH;
    }
}