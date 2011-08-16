package com.amee.platform.science;

public class CO2Amount extends Amount {

    public static final CO2Amount ZERO = new CO2Amount(0.0);

    /**
     * A CO2Amount representing the supplied value and default unit.
     *
     * @param amount
     *
     */
    public CO2Amount(String amount) {
        super(amount, CO2AmountUnit.DEFAULT);
    }

    /**
     * A CO2Amount representing the supplied value and default unit.
     *
     * @param value
     *
     */
    public CO2Amount(double value) {
        this(value, CO2AmountUnit.DEFAULT);
    }


    /**
     * A CO2Amount representing the supplied value and unit.
     *
     * @param value
     * @param unit
     *
     */
    public CO2Amount(double value, CO2AmountUnit unit) {
        super(value, unit);
    }
}
