package com.amee.domain.item.data;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "data_item_text_value")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DataItemTextValue extends BaseDataItemTextValue {

    public DataItemTextValue() {
        super();
    }

    public DataItemTextValue(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public DataItemTextValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, String value) {
        super(itemValueDefinition, dataItem, value);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DITV;
    }
}
