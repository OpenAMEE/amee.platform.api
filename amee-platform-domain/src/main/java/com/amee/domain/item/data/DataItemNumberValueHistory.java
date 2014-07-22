package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.HistoryValue;
import com.amee.platform.science.StartEndDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "data_item_number_value_history")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemNumberValueHistory extends BaseDataItemNumberValue implements HistoryValue {

    @Column(name = "start_date")
    private Date startDate = new Date();

    public DataItemNumberValueHistory() {
        super();
    }

    public DataItemNumberValueHistory(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public DataItemNumberValueHistory(ItemValueDefinition itemValueDefinition, DataItem dataItem, Double value) {
        super(itemValueDefinition, dataItem, value);
    }

    public DataItemNumberValueHistory(ItemValueDefinition itemValueDefinition, DataItem dataItem, Double value, Date startDate) {
        this(itemValueDefinition, dataItem, value);
        setStartDate(startDate);
    }

    @Override
    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        DataItemNumberValueHistory v = (DataItemNumberValueHistory) o;
        v.startDate = startDate;
    }

    @Override
    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    // Required for data binding. The parameter type of the setter must match the return type of the getter.
    public void setStartDate(StartEndDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DINVH;
    }
}