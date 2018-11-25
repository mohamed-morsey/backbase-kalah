package com.backbase.kalah.constant;

/**
 * Container for success and error messages
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
public final class Messages {

    //region generic error messages
    public static final String INVALID_ID_ERROR = "Invalid ID";
    public static final String INVALID_PARAMETER_ERROR = "Invalid parameter value";
    public static final String ITEM_NOT_FOUND_ERROR = "Item not found";
    public static final String ITEM_NULL_ERROR = "Item cannot be null";
    public static final String OPERATION_FAILURE_ERROR = "Operation failed";
    //endregion

    //region game-related error messages
    public static final String GAME_NULL_ERROR = "Game cannot be null";
    public static final String GAME_CREATION_FAILED_ERROR = "Failed to create a new game";
    //endregion

    //region logging messages
    public static final String NEW_BOARD_INITIALIZED_SUCCESSFULLY_MESSAGE = "New board initialized successfully";
    //endregion

    private Messages() {
        // Private constructor to prevent instantiation
    }
}
