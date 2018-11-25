package com.backbase.kalah.error;

/**
 * Thrown when an error occurs during the normal Kalah game operation
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public class KalahPlayException extends RuntimeException {
    public KalahPlayException(String message) {
        super(message);
    }

    public KalahPlayException(String message, Throwable exp) {
        super(message, exp);
    }
}
