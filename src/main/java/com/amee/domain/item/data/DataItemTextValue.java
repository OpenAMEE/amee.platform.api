package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DATA_ITEM_TEXT_VALUE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemTextValue extends BaseDataItemTextValue {

    public DataItemTextValue() {
        super();
    }

    public DataItemTextValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public DataItemTextValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem, String value) {
        super(itemValueDefinition, dataItem, value);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DITV;
    }
}
