package com.backbase.kalah.error;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.backbase.kalah.constant.Messages.INVALID_PARAMETER_ERROR;
import static com.backbase.kalah.constant.Messages.ITEM_NOT_FOUND_ERROR;
import static com.backbase.kalah.constant.Messages.OPERATION_FAILURE_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * Handler for exceptions thrown by REST controller
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 */
@ControllerAdvice
public class KalahGameExceptionHandler {
    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * Handler for {@link IllegalArgumentException} that can be thrown in case of invalid parameter is passed
     *
     * @param exp      Exception to be handled
     * @param response The response object
     * @throws IOException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    private void handleIllegalArgumentException(IllegalArgumentException exp, HttpServletResponse response) throws IOException {
        String errorMessage = INVALID_PARAMETER_ERROR + ": " + exp.getMessage();

        logger.error(errorMessage, exp);
        response.sendError(SC_BAD_REQUEST, errorMessage);
    }

    /**
     * Handler for {@link ObjectNotFoundException} that can be thrown if an item does not exist
     *
     * @param exp      Exception to be handled
     * @param response The response object
     * @throws IOException
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    private void handleResourceNotFoundException(ObjectNotFoundException exp, HttpServletResponse response) throws IOException {
        String errorMessage = ITEM_NOT_FOUND_ERROR + ": " + exp.getMessage();

        logger.error(errorMessage, exp);
        response.sendError(SC_NOT_FOUND, errorMessage);
    }

    /**
     * Handler for {@link KalahPlayException} that can be thrown if an item does not exist
     *
     * @param exp      Exception to be handled
     * @param response The response object
     * @throws IOException
     */
    @ExceptionHandler(KalahPlayException.class)
    private void handleKalahPlayException(KalahPlayException exp, HttpServletResponse response) throws IOException {
        String errorMessage = OPERATION_FAILURE_ERROR + ": " + exp.getMessage();

        logger.error(errorMessage, exp);
        response.sendError(SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
}
