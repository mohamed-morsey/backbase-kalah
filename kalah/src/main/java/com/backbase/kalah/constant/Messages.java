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
    public static final String GAME_NOT_FOUND_ERROR = "Game not found";
    public static final String GAME_CREATION_FAILED_ERROR = "Failed to create a new game";
    //endregion

    //region board-related error messages
    public static final String BOARD_NOT_FOUND_ERROR = "Board not found";
    public static final String INVALID_PIT_ID_ERROR = "Invalid pit ID";
    public static final String NOT_PLAYER_TURN_ERROR = "This is not that player's turn";
    public static final String GAME_FINISHED_ERROR = "Game is finished already";
    public static final String KALAH_MOVE_ERROR = "Playing from a Kalah is not allowed";
    //endregion

    //region logging messages
    public static final String NEW_BOARD_INITIALIZED_SUCCESSFULLY_MESSAGE = "New board initialized successfully";
    //endregion

    private Messages() {
        // Private constructor to prevent instantiation
    }
}
