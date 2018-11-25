package com.backbase.kalah.constant;

/**
 * Container for success and error messages
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
public final class Messages {

    //region success messages
    public static final String COUNT_CUSTOMERS_READ_SUCCESSFULLY = "%d customers read successfully";
    public static final String COUNT_ACCOUNTS_READ_SUCCESSFULLY = "%d accounts read successfully";
    public static final String COUNT_TRANSACTIONS_READ_SUCCESSFULLY = "%d transactions read successfully";

    public static final String CUSTOMER_CREATED_SUCCESSFULLY = "Customer created successfully";
    public static final String ACCOUNT_CREATED_SUCCESSFULLY = "Account created successfully";
    public static final String TRANSACTION_CREATED_SUCCESSFULLY = "Transaction created successfully";
    //endregion

    //region generic error messages
    public static final String INVALID_ID_ERROR = "Invalid ID";
    public static final String INVALID_PARAMETER_ERROR = "Invalid parameter value";
    public static final String OBJECT_NOT_FOUND_ERROR = "Object not found";
    public static final String OPERATION_FAILURE_ERROR = "Operation failed";
    //endregion

    //region logging messages
    public static final String NEW_GAME_INITIALIZED_SUCCESSFULLY_MESSAGE = "New game initialized successfully";
    //endregion

    private Messages() {
        // Private constructor to prevent instantiation
    }
}
