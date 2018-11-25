package com.backbase.kalah.service;

import com.backbase.kalah.constant.Constants;
import com.backbase.kalah.error.KalahPlayException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.model.PlayerTurn;
import com.backbase.kalah.repository.BoardRepository;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;
import static com.backbase.kalah.constant.Constants.COUNT_PLAYER_PITS;
import static com.backbase.kalah.constant.Constants.INITIAL_STONE_COUNT;
import static com.backbase.kalah.constant.Messages.BOARD_NOT_FOUND_ERROR;
import static com.backbase.kalah.constant.Messages.GAME_FINISHED_ERROR;
import static com.backbase.kalah.constant.Messages.INVALID_PIT_ID_ERROR;
import static com.backbase.kalah.constant.Messages.ITEM_NOT_FOUND_ERROR;
import static com.backbase.kalah.constant.Messages.KALAH_MOVE_ERROR;
import static com.backbase.kalah.constant.Messages.NEW_BOARD_INITIALIZED_SUCCESSFULLY_MESSAGE;
import static com.backbase.kalah.constant.Messages.NOT_PLAYER_TURN_ERROR;
import static com.backbase.kalah.constant.Messages.PIT_EMPTY_ERROR;
import static com.backbase.kalah.constant.Messages.PLAY_AGAIN_MESSAGE;
import static com.backbase.kalah.model.PlayerTurn.FIRST_PLAYER;
import static com.backbase.kalah.model.PlayerTurn.SECOND_PLAYER;
import static com.backbase.kalah.model.Status.RUNNING;

/**
 * A service for managing kalah {@link Board}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Service
public class BoardService implements CrudService<Board> {
    private Logger logger;
    private BoardRepository boardRepository;

    @Inject
    public BoardService(BoardRepository boardRepository, Logger logger) {
        this.boardRepository = boardRepository;
        this.logger = logger;
    }

    @Override
    public Optional<Board> get(long id) {
        return Optional.of(boardRepository.findOne(id));
    }

    @Override
    public List<Board> getAll() {
        return ImmutableList.copyOf(boardRepository.findAll());
    }

    @Override
    public Board create(Board item) {
        return boardRepository.save(item);
    }

    @Override
    public boolean update(Board item) {
        long id = item.getId();

        if (!boardRepository.exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        boardRepository.save(item);
        return true;
    }

    @Override
    public boolean delete(long id) {
        if (!boardRepository.exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        boardRepository.delete(id);
        return true;
    }

    /**
     * Creates a new initialized {@link Board}
     *
     * @return The newly created board
     */
    public Board createInitializedBoard() {
        Board board = new Board();

        List<Pit> player1Pits = initPlayerPits(board, 0, COUNT_PLAYER_PITS - 1, COUNT_OF_ALL_PITS - 2);
        List<Pit> player2Pits = initPlayerPits(board, COUNT_PLAYER_PITS, COUNT_OF_ALL_PITS - 1, COUNT_PLAYER_PITS - 2);

        List<Pit> allPits = new ArrayList<>(player1Pits);
        allPits.addAll(player2Pits);

        board.setPits(ImmutableList.copyOf(allPits));
        logger.info(NEW_BOARD_INITIALIZED_SUCCESSFULLY_MESSAGE);

        return create(board);
    }

    /**
     * Makes a move on the given board
     *
     * @param id    The ID of the board
     * @param pitId The ID of the pit to be used for making the move
     * @return The status of the board after making move if successful, {@link Optional#EMPTY} otherwise
     */
    public Optional<Board> makeMove(long id, int pitId) {
        Preconditions.checkArgument(pitId >= 0 && pitId < COUNT_OF_ALL_PITS, INVALID_PIT_ID_ERROR);

        Optional<Board> boardOptional = get(id);
        if (!boardOptional.isPresent()) {
            logger.warn(BOARD_NOT_FOUND_ERROR);
            return Optional.empty();
        }

        Board board = boardOptional.get();

        // check if the game is still running
        if (board.getStatus() != RUNNING) {
            logger.warn(GAME_FINISHED_ERROR);
            throw new KalahPlayException(GAME_FINISHED_ERROR);
        }

        // Check if the player wants to play from a Kalah
        if (isKalah(pitId)) {
            logger.warn(KALAH_MOVE_ERROR);
            throw new KalahPlayException(KALAH_MOVE_ERROR);
        }

        // Check if it is the player's turn
        if (!isPlayerTurn(board, pitId)) {
            logger.warn(NOT_PLAYER_TURN_ERROR);
            throw new KalahPlayException(NOT_PLAYER_TURN_ERROR);
        }

        Pit desiredPit = board.getPits().get(pitId);
        int stones = desiredPit.getStoneCount();
        if (stones == 0) {
            logger.warn(PIT_EMPTY_ERROR);
            throw new KalahPlayException(PIT_EMPTY_ERROR + ", " + PLAY_AGAIN_MESSAGE);
        }

        return Optional.of(makeMove(board, desiredPit));
    }

    /**
     * Makes the actual move
     *
     * @param board The board
     * @param pit   The pit to be used
     * @return The new board status
     */
    private Board makeMove(Board board, Pit pit) {
        int stones = pit.getStoneCount();
        pit.setStoneCount(0); // Set stone count to 0 as we should empty the desired pit

        Pit lastVisitedPit = pit; // represents the last pit that received a stone
        Pit nextPit = board.getPits().get(pit.getNextPitIndex());

        for (int i = 0; i < stones; i++) {
            nextPit.incrementStones();
            lastVisitedPit = nextPit;
            nextPit = board.getPits().get(nextPit.getNextPitIndex());
        }

        // If the last one is placed into Kalah, then we should not switch players, and no furthe actions are required
        if(lastVisitedPit.isKalah()){
            return boardRepository.save(board);
        }

        // If the last one is placed into Kalah, then we SHOULD NOT SWITCH PLAYERS
        boolean switchPlayer = !lastVisitedPit.isKalah();

        // Switch player turn and save board, no further actions are required
        if (switchPlayer) {
            PlayerTurn nextPlayerTurn = board.getPlayerTurn() == FIRST_PLAYER ? SECOND_PLAYER : FIRST_PLAYER;
            board.setPlayerTurn(nextPlayerTurn);
        }

        // If the last placed stone is dropped into an empty pit, then we should collect all opposite pit stones if not zero
        if(lastVisitedPit.getStoneCount() == 1){
            Pit oppositePit = board.getPits().get(lastVisitedPit.getOppositePitIndex());
            if(oppositePit.getStoneCount() != 0){
                int totalStones = lastVisitedPit.getStoneCount() + oppositePit.getStoneCount();

                // clear stones in both pits and move them to the right Kalah
                lastVisitedPit.setStoneCount(0);
                oppositePit.setStoneCount(0);

                int kalahStones = board.getPits().get(6).getStoneCount();
                board.getPits().get(6).setStoneCount(kalahStones + totalStones);
            }
        }

        return boardRepository.save(board);
    }

    /**
     * Initialize the pits of a player
     *
     * @param board                 The board to which the pits belong
     * @param startPitIndex         The start index of the player pits
     * @param endPitIndex           The end index of the player pits
     * @param oppositePitStartIndex The start index of the opposite pits (each pit should also keep track of its opposite pit)
     * @return A list of the pits of a player initialized with {@link Constants#INITIAL_STONE_COUNT}
     */
    private List<Pit> initPlayerPits(Board board, int startPitIndex, int endPitIndex, int oppositePitStartIndex) {
        List<Pit> playerPits = new ArrayList<>();

        int oppositePitIndex = oppositePitStartIndex; // The index of the opposite pit

        // Initialize all pits except Kalah
        for (int i = startPitIndex; i < endPitIndex; i++) {
            Pit currentPit = new Pit(i, i + 1, oppositePitIndex, INITIAL_STONE_COUNT, false);
            currentPit.setBoard(board);
            playerPits.add(currentPit);

            oppositePitIndex--;
        }

        // If the kalah of the player is ranked as 6 then the opposite one is ranked 0 and vice versa
        int oppositeKalahIndex = (endPitIndex == COUNT_PLAYER_PITS - 1) ? COUNT_OF_ALL_PITS - 1 : COUNT_PLAYER_PITS - 1;

        // Initialize the Kalah
        Pit kalahPit = new Pit(endPitIndex, (endPitIndex + 1) % COUNT_OF_ALL_PITS, oppositeKalahIndex, 0, true);
        kalahPit.setBoard(board);
        playerPits.add(kalahPit);

        return playerPits;
    }

    /**
     * Checks if the pits is a Kalah (house)
     *
     * @param pitId The ID of the pit
     * @return True if the pit is a Kalah, false otherwise
     */
    private boolean isKalah(int pitId) {
        return (pitId == COUNT_PLAYER_PITS - 1) || (pitId == COUNT_OF_ALL_PITS - 1);
    }

    /**
     * Checks if the desired pit is allowed for the current player
     *
     * @param board The board
     * @param pitId The ID of the pit
     * @return True if it's player's turn, false otherwise
     */
    private boolean isPlayerTurn(Board board, int pitId) {
        // In case of first player the allowed pits are between 0 and 5
        if ((board.getPlayerTurn() == FIRST_PLAYER) && (pitId >= 0 && pitId < COUNT_PLAYER_PITS - 1)) {
            return true;
        }

        // In case of first player the allowed pits are between 7 and 12
        if ((board.getPlayerTurn() == SECOND_PLAYER) && (pitId >= COUNT_PLAYER_PITS && pitId < COUNT_OF_ALL_PITS - 1)) {
            return true;
        }

        return false;
    }
}
