package com.backbase.kalah.constant;

/**
 * Container for column, field and parameter names
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
public final class Fields {

    // region field names of customer
    public static final String NAME_FIELD = "name";
    public static final String SURNAME_FIELD = "surname";
    public static final String ADDRESS_FIELD = "address";
    public static final String POSTCODE_FIELD = "postcode";
    // endregion

    // region field name of account
    public static final String CREDIT_FIELD = "credit";
    public static final String CUSTOMER_FIELD = "customer";
    public static final String CUSTOMER_ID_PARAMETER = "customerId";
    // endregion

    // region field names of transaction
    public static final String ACCOUNT_ID_PARAMETER = "accountId";
    // endregion

    private Fields() {
        // Private constructor to prevent instantiation
    }
}
