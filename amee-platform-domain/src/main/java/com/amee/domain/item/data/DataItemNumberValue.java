package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "data_item_number_value")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemNumberValue extends BaseDataItemNumberValue {

    public DataItemNumberValue() {
        super();
    }

    public DataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public DataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, Double value) {
        super(itemValueDefinition, dataItem, value);
    }

    public DataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, Integer value) {
        super(itemValueDefinition, dataItem, value);
    }

    public DataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, String value) {
        super(itemValueDefinition, dataItem, value);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DINV;
    }
}
