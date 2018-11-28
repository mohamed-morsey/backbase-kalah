package com.backbase.kalah.error;

/**
 * Thrown when an error occurs during the normal Kalah game operation
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public class KalahGameException extends RuntimeException {
    public KalahGameException(String message) {
        super(message);
    }
}
