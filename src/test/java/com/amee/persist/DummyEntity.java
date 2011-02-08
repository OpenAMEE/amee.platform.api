package com.amee.persist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DUMMY_ENTITY")
public class DummyEntity extends BaseEntity {

    @Column(name = "DUMMY_TEXT", length = 100, nullable = false)
    private String dummyText = "";

    public DummyEntity() {
        super();
    }

    public DummyEntity(String dummyText) {
        super();
        setDummyText(dummyText);
    }

    public String getDummyText() {
        return dummyText;
    }

    public void setDummyText(String dummyText) {
        this.dummyText = dummyText;
    }
}