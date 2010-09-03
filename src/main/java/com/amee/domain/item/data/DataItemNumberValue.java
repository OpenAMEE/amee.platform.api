package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DATA_ITEM_NUMBER_VALUE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemNumberValue extends BaseDataItemNumberValue {

    public DataItemNumberValue() {
        super();
    }

    public DataItemNumberValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem, Double value) {
        super(itemValueDefinition, dataItem, value);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DINV;
    }
}
