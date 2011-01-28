/*
 * This file is part of AMEE.
 *
 * Copyright (c) 2007, 2008, 2009 AMEE UK LIMITED (help@amee.com).
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
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
import com.amee.domain.item.data.NuDataItem;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;

import java.io.Serializable;

// TODO: Refactor (see other Enums)

public enum ObjectType implements Serializable {

    DC, AL, ID, IVD, DI, PI, IV, PR, ALC, USR, GRP, ENV, PRM, LN, GP, VD, AV, MD, TA, ET, RVD, DINV, DINVH, DITV, DITVH, PINV, PITV, NPI, NDI;

    private String[] names = {
            "DC",
            "AL",
            "ID",
            "IVD",
            "DI",
            "PI",
            "IV",
            "PR",
            "ALC",
            "USR",
            "GRP",
            "ENV",
            "PRM",
            "LN",
            "GP",
            "VD",
            "AV",
            "MD",
            "TA",
            "ET",
            "RVD",
            "DINV",
            "DINVH",
            "DITV",
            "DITVH",
            "PINV",
            "PITV",
            "NPI",
            "NDI"};

    private String[] labels = {
            "DataCategory",
            "Algorithm",
            "ItemDefinition",
            "ItemValueDefinition",
            "DataItem",
            "ProfileItem",
            "ItemValue",
            "Profile",
            "AlgorithmContext",
            "User",
            "Group",
            "Environment",
            "Permission",
            "LocaleName",
            "GroupPrincipal",
            "ValueDefinition",
            "APIVersion",
            "Metadata",
            "Tag",
            "EntityTag",
            "ReturnValueDefinition",
            "DataItemNumberValue",
            "DataItemNumberValueHistory",
            "DataItemTextValue",
            "DataItemTextValueHistory",
            "ProfileItemNumberValue",
            "ProfileItemTextValue",
            "NewProfileItem",
            "NewDataItem"};

    public String toString() {
        return getName();
    }

    public String getName() {
        return names[this.ordinal()];
    }

    public String getLabel() {
        return labels[this.ordinal()];
    }

    public static ObjectType getType(Class c) {
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
        } else if (NuDataItem.class.isAssignableFrom(c)) {
            return DI;
        } else if (NuProfileItem.class.isAssignableFrom(c)) {
            return PI;
        } else if (BaseItemValue.class.isAssignableFrom(c)) {
            return IV;
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
        } else if (NuDataItem.class.isAssignableFrom(c)) {
            return NuDataItem.class;
        } else if (NuProfileItem.class.isAssignableFrom(c)) {
            return NuProfileItem.class;
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
        }
        throw new IllegalArgumentException("Class not supported.");
    }

    public Class getClazz() {
        if (this.equals(USR)) {
            return User.class;
        } else if (this.equals(GRP)) {
            return Group.class;
        } else if (this.equals(ENV)) {
            return Environment.class;
        } else if (this.equals(PR)) {
            return Profile.class;
        } else if (this.equals(DC)) {
            return DataCategory.class;
        } else if (this.equals(DI)) {
            return NuDataItem.class;
        } else if (this.equals(PI)) {
            return NuProfileItem.class;
        } else if (this.equals(IV)) {
            return BaseItemValue.class;
        } else if (this.equals(MD)) {
            return Metadata.class;
        } else if (this.equals(TA)) {
            return Tag.class;
        } else if (this.equals(ET)) {
            return EntityTag.class;
        }
        throw new IllegalArgumentException("Class not supported.");
    }
}