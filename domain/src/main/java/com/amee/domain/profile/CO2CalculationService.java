package com.amee.domain.profile;

import com.amee.domain.item.profile.ProfileItem;
import org.springframework.stereotype.Service;

/**
 * Minimal service interface allowing a ProfileItem to calculate it's own CO2 Amount.
 * <p/>
 * Note: the interface is required to mitigate circular dependencies between the amee-calculation and amee-platform-domain
 * packages.
 */
@Service
public interface CO2CalculationService {

    /**
     * Calculate the {@link com.amee.platform.science.CO2Amount CO2Amount} of a ProfileItem. The calculated value is
     * set into the passed {@link com.amee.domain.item.profile.ProfileItem}.
     *
     * @param profileItem - the {@link com.amee.domain.item.profile.ProfileItem} for which to calculate the
     * {@link com.amee.platform.science.CO2Amount CO2Amount}
     */
    void calculate(ProfileItem profileItem);
}
