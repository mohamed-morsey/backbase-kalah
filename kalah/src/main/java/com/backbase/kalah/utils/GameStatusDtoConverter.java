package com.backbase.kalah.utils;

import com.backbase.kalah.dto.GameStatusDto;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.model.Pit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohamed Morsey
 * Date: 2018-11-26
 **/
public class GameStatusDtoConverter {
    private GameStatusDtoConverter() {
        // private constructor to prevent instantiation
    }

    public static GameStatusDto toGameStatusDto(Game game) {
        Map<String, String> boardStatus = new HashMap<>();
        Board gameBoard = game.getBoard();

        for (Pit pit : gameBoard.getPits()) {
            // We add 1 here as the output should be 1 based not 0 based
            boardStatus.put(String.valueOf(pit.getIndex() + 1), String.valueOf(pit.getStoneCount()));
        }

        return new GameStatusDto(game.getId(), game.getUri(), boardStatus);
    }
}
