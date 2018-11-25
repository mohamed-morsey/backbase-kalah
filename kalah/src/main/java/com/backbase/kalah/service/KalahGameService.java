package com.backbase.kalah.service;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.repository.BoardRepository;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;
import static com.backbase.kalah.constant.Constants.COUNT_PLAYER_PITS;
import static com.backbase.kalah.constant.Constants.INITIAL_STONE_COUNT;
import static com.backbase.kalah.constant.Messages.NEW_GAME_INITIALIZED_SUCCESSFULLY_MESSAGE;

/**
 * A service for handling Kalah game
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Service
public class KalahGameService {
    private Logger logger;
    private BoardRepository boardRepository;

    @Inject
    public KalahGameService(BoardRepository boardRepository, Logger logger) {
        this.boardRepository = boardRepository;
        this.logger = logger;
    }

    public Game createNewGame(){
        Board board = new Board();

        List<Pit> player1Pits = initPlayerPits(board, 0, COUNT_PLAYER_PITS - 1, COUNT_OF_ALL_PITS - 2);
        List<Pit> player2Pits = initPlayerPits(board, COUNT_PLAYER_PITS, COUNT_OF_ALL_PITS - 1, COUNT_PLAYER_PITS - 2);

        List<Pit> allPits = new ArrayList<>(player1Pits);
        allPits.addAll(player2Pits);

        board.setPits(ImmutableList.copyOf(allPits));

        logger.info(NEW_GAME_INITIALIZED_SUCCESSFULLY_MESSAGE);

        boardRepository.save(board);

        return new Game();
    }

    private List<Pit> initPlayerPits(Board board, int startPitIndex, int endPitIndex, int oppositePitStartIndex){
        List<Pit> playerPits = new ArrayList<>();

        int oppositePitIndex = oppositePitStartIndex; // The index of the opposite pit

        // Initialize all pits except Kalah
        for (int i = startPitIndex; i < endPitIndex; i++){
            Pit currentPit = new Pit(i, i + 1, oppositePitIndex, INITIAL_STONE_COUNT, false);
            currentPit.setBoard(board);
            playerPits.add(currentPit);

            oppositePitIndex--;
        }

        // If the kalah of the player is ranked as 6 then the opposite one is ranked 0 and vice versa
        int oppositeKalahIndex = (endPitIndex == COUNT_PLAYER_PITS - 1) ? COUNT_OF_ALL_PITS -1 : COUNT_PLAYER_PITS - 1;

        // Initialize the Kalah
        Pit kalahPit = new Pit(endPitIndex, (endPitIndex + 1) % COUNT_OF_ALL_PITS, oppositeKalahIndex, 0, true);
        kalahPit.setBoard(board);
        playerPits.add(kalahPit);

        return playerPits;
    }

}
