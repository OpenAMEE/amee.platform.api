package com.amee.domain;

import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.algorithm.AlgorithmContext;
import com.amee.domain.auth.Group;
import com.amee.domain.auth.GroupPrincipal;
import com.amee.domain.auth.Permission;
import com.amee.domain.auth.User;
import com.amee.domain.data.*;
import com.amee.domain.environment.Environment;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.*;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.item.profile.ProfileItemNumberValue;
import com.amee.domain.item.profile.ProfileItemTextValue;
import com.amee.domain.profile.Profile;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum ObjectType implements Serializable {

    // NOTE: These are stored in the database as strings. See: com.amee.domain.AMEEEntityReference.entityType
    DC("DC", "DataCategory") { public Class toClass() {return DataCategory.class;} },
    AL("AL", "Algorithm") { public Class toClass() {return Algorithm.class;} },
    ID("ID", "ItemDefinition") { public Class toClass() {return ItemDefinition.class;} },
    IVD("IVD", "ItemValueDefinition") { public Class toClass() {return ItemValueDefinition.class;} },
    PR("PR", "Profile") { public Class toClass() {return Profile.class;} },
    ALC("ALC", "AlgorithmContext") { public Class toClass() {return AlgorithmContext.class;} },
    USR("USR", "User") { public Class toClass() {return User.class;} },
    GRP("GRP", "Group") { public Class toClass() {return Group.class;} },
    ENV("ENV", "Environment") { public Class toClass() {return Environment.class;} },
    PRM("PRM", "Permission") { public Class toClass() {return Permission.class;} },
    LN("LN", "LocaleName") { public Class toClass() {return LocaleName.class;} },
    GP("GP", "GroupPrincipal") { public Class toClass() {return GroupPrincipal.class;} },
    VD("VD", "ValueDefinition") { public Class toClass() {return ValueDefinition.class;} },
    AV("AV", "APIVersion") { public Class toClass() {return APIVersion.class;} },
    MD("MD", "Metadata") { public Class toClass() {return Metadata.class;} },
    TA("TA", "Tag") { public Class toClass() {return Tag.class;} },
    ET("ET", "EntityTag") { public Class toClass() {return EntityTag.class;} },
    RVD("RVD", "ReturnValueDefinition") { public Class toClass() {return ReturnValueDefinition.class;} },
    DINV("DINV", "DataItemNumberValue") { public Class toClass() {return DataItemNumberValue.class;} },
    DINVH("DINVH", "DataItemNumberValueHistory") { public Class toClass() {return DataItemNumberValueHistory.class;} },
    DITV("DITV", "DataItemTextValue") { public Class toClass() {return DataItemTextValue.class;} },
    DITVH("DITVH", "DataItemTextValueHistory") { public Class toClass() {return DataItemTextValueHistory.class;} },
    PINV("PINV", "ProfileItemNumberValue") { public Class toClass() {return ProfileItemNumberValue.class;} },
    PITV("PITV", "ProfileItemTextValue") { public Class toClass() {return ProfileItemTextValue.class;} },
    PI("PI", "ProfileItem") { public Class toClass() {return ProfileItem.class;} },
    DI("DI", "DataItem") { public Class toClass() {return DataItem.class;} },
    UN("UN", "Unit") { public Class toClass() {return AMEEUnit.class;} },
    UT("UT", "UnitType") { public Class toClass() {return AMEEUnitType.class;} };

    private final String name;
    private final String label;

    private static final Map<String, ObjectType> stringToEnum = new HashMap<String, ObjectType>();

    static {
        for (ObjectType type : values()) {
            stringToEnum.put(type.toString(), type);
        }
    }

    ObjectType(String name, String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public String toString() {
        return name;
    }

    public static ObjectType fromString(String name) {
        return stringToEnum.get(name);
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public abstract Class toClass();
    
    public static ObjectType fromClass(Class c) {
        if (DataCategory.class.isAssignableFrom(c)) {
            return DC;
        } else if (Algorithm.class.isAssignableFrom(c)) {
            return AL;
        } else if (ItemDefinition.class.isAssignableFrom(c)) {
            return ID;
        } else if (ItemValueDefinition.class.isAssignableFrom(c)) {
            return IVD;
        } else if (ReturnValueDefinition.class.isAssignableFrom(c)) {
            return RVD;
        } else if (DataItem.class.isAssignableFrom(c)) {
            return DI;
        } else if (ProfileItem.class.isAssignableFrom(c)) {
            return PI;
        } else if (DataItemNumberValue.class.isAssignableFrom(c)) {
            return DINV;
        } else if (DataItemNumberValueHistory.class.isAssignableFrom(c)) {
            return DINVH;
        } else if (DataItemTextValue.class.isAssignableFrom(c)) {
            return DITV;
        } else if (DataItemTextValueHistory.class.isAssignableFrom(c)) {
            return DITVH;
        } else if (ProfileItemNumberValue.class.isAssignableFrom(c)) {
            return PINV;
        } else if (ProfileItemTextValue.class.isAssignableFrom(c)) {
           return PITV;
        } else if (Profile.class.isAssignableFrom(c)) {
            return PR;
        } else if (AlgorithmContext.class.isAssignableFrom(c)) {
            return ALC;
        } else if (User.class.isAssignableFrom(c)) {
            return USR;
        } else if (Group.class.isAssignableFrom(c)) {
            return GRP;
        } else if (Environment.class.isAssignableFrom(c)) {
            return ENV;
        } else if (Permission.class.isAssignableFrom(c)) {
            return PRM;
        } else if (LocaleName.class.isAssignableFrom(c)) {
            return LN;
        } else if (GroupPrincipal.class.isAssignableFrom(c)) {
            return GP;
        } else if (ValueDefinition.class.isAssignableFrom(c)) {
            return VD;
        } else if (APIVersion.class.isAssignableFrom(c)) {
            return AV;
        } else if (Metadata.class.isAssignableFrom(c)) {
            return MD;
        } else if (Tag.class.isAssignableFrom(c)) {
            return TA;
        } else if (EntityTag.class.isAssignableFrom(c)) {
            return ET;
        } else if (AMEEUnit.class.isAssignableFrom(c)) {
            return UN;
        } else if (AMEEUnitType.class.isAssignableFrom(c)) {
            return UT;
        }
        throw new IllegalArgumentException("Class not supported.");
    }

    /**
     * Convert the supplied class into a 'real' class. Useful for classes which have been
     * mangled by Hibernate.
     *
     * @param c class you want to convert to the real class
     * @return the real class, based on the supplied class
     */
    public static Class getClazz(Class c) {
        if (DataCategory.class.isAssignableFrom(c)) {
            return DataCategory.class;
        } else if (Algorithm.class.isAssignableFrom(c)) {
            return Algorithm.class;
        } else if (ItemDefinition.class.isAssignableFrom(c)) {
            return ItemDefinition.class;
        } else if (ItemValueDefinition.class.isAssignableFrom(c)) {
            return ItemValueDefinition.class;
        } else if (ReturnValueDefinition.class.isAssignableFrom(c)) {
            return ReturnValueDefinition.class;
        } else if (DataItem.class.isAssignableFrom(c)) {
            return DataItem.class;
        } else if (ProfileItem.class.isAssignableFrom(c)) {
            return ProfileItem.class;
        } else if (BaseItemValue.class.isAssignableFrom(c)) {
            return BaseItemValue.class;
        } else if (Profile.class.isAssignableFrom(c)) {
            return Profile.class;
        } else if (AlgorithmContext.class.isAssignableFrom(c)) {
            return AlgorithmContext.class;
        } else if (User.class.isAssignableFrom(c)) {
            return User.class;
        } else if (Group.class.isAssignableFrom(c)) {
            return Group.class;
        } else if (Environment.class.isAssignableFrom(c)) {
            return Environment.class;
        } else if (Permission.class.isAssignableFrom(c)) {
            return Permission.class;
        } else if (LocaleName.class.isAssignableFrom(c)) {
            return LocaleName.class;
        } else if (GroupPrincipal.class.isAssignableFrom(c)) {
            return GroupPrincipal.class;
        } else if (ValueDefinition.class.isAssignableFrom(c)) {
            return ValueDefinition.class;
        } else if (APIVersion.class.isAssignableFrom(c)) {
            return APIVersion.class;
        } else if (Metadata.class.isAssignableFrom(c)) {
            return Metadata.class;
        } else if (Tag.class.isAssignableFrom(c)) {
            return Tag.class;
        } else if (EntityTag.class.isAssignableFrom(c)) {
            return EntityTag.class;
        } else if (AMEEUnit.class.isAssignableFrom(c)) {
            return AMEEUnit.class;
        } else if (AMEEUnitType.class.isAssignableFrom(c)) {
            return AMEEUnitType.class;
        }
        throw new IllegalArgumentException("Class not supported.");
    }
}