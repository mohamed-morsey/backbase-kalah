package com.backbase.kalah.service;

import com.backbase.kalah.constant.Constants;
import com.backbase.kalah.error.KalahPlayException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.model.enums.GameResult;
import com.backbase.kalah.model.enums.PlayerTurn;
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
import static com.backbase.kalah.constant.Constants.COUNT_OF_PLAYER_PITS;
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
import static com.backbase.kalah.model.enums.PlayerTurn.FIRST_PLAYER;
import static com.backbase.kalah.model.enums.PlayerTurn.SECOND_PLAYER;
import static com.backbase.kalah.model.enums.Status.FINISHED;
import static com.backbase.kalah.model.enums.Status.RUNNING;

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
        return Optional.ofNullable(boardRepository.findOne(id));
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
     * Creates a new initialized {@link Board} and saves it into database
     *
     * @return The newly created board
     */
    public Board createInitializedBoard() {
        Board board = initBoard();

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
     * Creates a new initialized {@link Board}
     *
     * @return The newly created board
     */
    Board initBoard() {
        Board board = new Board();

        List<Pit> player1Pits = initPlayerPits(board, 0, COUNT_OF_PLAYER_PITS - 1, COUNT_OF_ALL_PITS - 2);
        List<Pit> player2Pits = initPlayerPits(board, COUNT_OF_PLAYER_PITS, COUNT_OF_ALL_PITS - 1, COUNT_OF_PLAYER_PITS - 2);

        List<Pit> allPits = new ArrayList<>(player1Pits);
        allPits.addAll(player2Pits);

        board.setPits(allPits);
        logger.info(NEW_BOARD_INITIALIZED_SUCCESSFULLY_MESSAGE);
        return board;
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

        PlayerTurn playerTurn = board.getPlayerTurn();
        Pit lastVisitedPit = pit; // represents the last pit that received a stone
        Pit nextPit = board.getPits().get(pit.getNextPitIndex());

        int i = 0;
        while (i < stones) {
            // If it is not my own Kalah, then I should not drop a stone into it
            if (!isOpponentKalah(playerTurn, nextPit)) {
                nextPit.incrementStones(1);
                i++;
            }

            lastVisitedPit = nextPit;
            nextPit = board.getPits().get(nextPit.getNextPitIndex());
        }

        // If the last one is placed into my Kalah, then we should not switch players, and no further actions are required
        if (isMyKalah(playerTurn, lastVisitedPit)) {
            return boardRepository.save(board);
        }

        // Switch player turn
        PlayerTurn nextPlayerTurn = playerTurn == FIRST_PLAYER ? SECOND_PLAYER : FIRST_PLAYER;
        board.setPlayerTurn(nextPlayerTurn);

        // If the last placed stone is dropped into an empty pit (one of her own pits not her opponents)
        // then we should collect all opposite pit stones if not zero
        if ((lastVisitedPit.getStoneCount() == 1) && (isMyOwnNormalPit(playerTurn, lastVisitedPit))) {
            Pit oppositePit = board.getPits().get(lastVisitedPit.getOppositePitIndex());
            if (oppositePit.getStoneCount() != 0) {
                int totalStones = lastVisitedPit.getStoneCount() + oppositePit.getStoneCount();

                // clear stones in both pits and move them to the right Kalah
                lastVisitedPit.setStoneCount(0);
                oppositePit.setStoneCount(0);

                Pit myKalah = getMyKalah(board, playerTurn);
                int kalahStones = myKalah.getStoneCount();
                myKalah.setStoneCount(kalahStones + totalStones);
            }
        }

        boolean isFinished = isGameFinished(board);
        if (isFinished) {
            board.setStatus(FINISHED);
            collectAllRemainingStones(board);
            GameResult winner = getWinningPlayer(board);
            logger.info(winner);
        }
        return boardRepository.save(board);
    }

    /**
     * Gets the winner of the game
     *
     * @param board The board
     * @return {@link PlayerTurn#FIRST_PLAYER} if first player is the winner, {@link PlayerTurn#SECOND_PLAYER} otherwise
     */
    private GameResult getWinningPlayer(Board board) {
        Pit player1Kalah = getMyKalah(board, FIRST_PLAYER);
        Pit player2Kalah = getMyKalah(board, SECOND_PLAYER);

        if (player1Kalah.getStoneCount() > player2Kalah.getStoneCount()) {
            return GameResult.FIRST_PLAYER;
        } else if (player1Kalah.getStoneCount() < player2Kalah.getStoneCount()) {
            return GameResult.SECOND_PLAYER;
        }

        return GameResult.TIE;
    }

    /**
     * If a game is finished, i.e. when a player has no more stones in any of her pits, then we should collect all stones
     * of the opponent and drop them into her Kalah
     *
     * @param board The board to be used for collecting stones
     */
    private void collectAllRemainingStones(Board board) {
        // Collect all stones in all pits of player1
        int player1RemainingStones = 0;
        for (int i = 0; i < COUNT_OF_PLAYER_PITS - 1; i++) {
            player1RemainingStones += board.getPits().get(i).getStoneCount();
            board.getPits().get(i).setStoneCount(0);
        }

        Pit player1Kalah = getMyKalah(board, FIRST_PLAYER);
        player1Kalah.incrementStones(player1RemainingStones);

        // Collect all stones in all pits of player2
        int player2RemainingStones = 0;
        for (int i = COUNT_OF_PLAYER_PITS; i < COUNT_OF_ALL_PITS - 1; i++) {
            player2RemainingStones += board.getPits().get(i).getStoneCount();
            board.getPits().get(i).setStoneCount(0);
        }

        Pit player2Kalah = getMyKalah(board, SECOND_PLAYER);
        player2Kalah.incrementStones(player2RemainingStones);
    }

    /**
     * Checks if the game has come to an end, i.e. when a player has no more stones in any of her pits
     *
     * @param board The board to be checked
     * @return True if the game is finished, false otherwise
     */
    private boolean isGameFinished(Board board) {
        // Check if player1 has all empty pits
        boolean player1AllEmpty = true;
        for (int i = 0; i < COUNT_OF_PLAYER_PITS - 1; i++) {
            player1AllEmpty = player1AllEmpty && board.getPits().get(i).getStoneCount() == 0;
        }

        boolean player2AllEmpty = true;
        for (int i = COUNT_OF_PLAYER_PITS; i < COUNT_OF_ALL_PITS - 1; i++) {
            player2AllEmpty = player2AllEmpty && board.getPits().get(i).getStoneCount() == 0;
        }

        // Check if one player has no more stones
        return player1AllEmpty || player2AllEmpty;
    }

    /**
     * Checks if the pit is one of current player's normal pits (Non Kalah pit)
     *
     * @param currentTurn The player turn
     * @param pitToCheck  The {@link Pit} to be checked
     * @return True if the it's one of the player's pits except Kalah
     */
    private boolean isMyOwnNormalPit(PlayerTurn currentTurn, Pit pitToCheck) {
        if (pitToCheck.isKalah()) {
            return false;
        }

        // First player's pits are ranged between 0 and 5
        if ((currentTurn == FIRST_PLAYER) && (pitToCheck.getIndex() >= 0)
                && (pitToCheck.getIndex() < COUNT_OF_PLAYER_PITS - 1)) {
            return true;
        }

        // First player's pits are ranged between 7 and 12
        if ((currentTurn == SECOND_PLAYER) && (pitToCheck.getIndex() >= COUNT_OF_PLAYER_PITS)
                && (pitToCheck.getIndex() < COUNT_OF_ALL_PITS - 1)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the pit is current player's Kalah
     *
     * @param currentTurn The player turn
     * @param pitToCheck  The {@link Pit} to be checked
     * @return True if the it's the player's Kalah, false otherwise
     */
    private boolean isMyKalah(PlayerTurn currentTurn, Pit pitToCheck) {
        // First player's Kalah is index at 6
        if ((currentTurn == FIRST_PLAYER) && (pitToCheck.getIndex() == COUNT_OF_PLAYER_PITS - 1)) {
            return true;
        }

        // Second player's Kalah is index at 13
        if ((currentTurn == SECOND_PLAYER) && (pitToCheck.getIndex() == COUNT_OF_ALL_PITS - 1)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the pit is opponent player's Kalah
     *
     * @param currentTurn The player turn
     * @param pitToCheck  The {@link Pit} to be checked
     * @return True if the it's the opponent player's Kalah, false otherwise
     */
    private boolean isOpponentKalah(PlayerTurn currentTurn, Pit pitToCheck) {
        // If first player's Kalah then her opponent's Kalah is at index 13
        if ((currentTurn == FIRST_PLAYER) && (pitToCheck.getIndex() == COUNT_OF_ALL_PITS - 1)) {
            return true;
        }

        // If second player's Kalah then her opponent's Kalah is at index 6
        if ((currentTurn == SECOND_PLAYER) && (pitToCheck.getIndex() == COUNT_OF_PLAYER_PITS - 1)) {
            return true;
        }

        return false;
    }

    /**
     * returns the Kalah of the current player
     *
     * @param board       The board
     * @param currentTurn The player turn
     * @return The Kalah pit of the current
     */
    private Pit getMyKalah(Board board, PlayerTurn currentTurn) {
        Pit playerKalah = (currentTurn == FIRST_PLAYER) ? board.getPits().get(COUNT_OF_PLAYER_PITS - 1) : board.getPits().get(COUNT_OF_ALL_PITS - 1);
        return playerKalah;
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
        int oppositeKalahIndex = (endPitIndex == COUNT_OF_PLAYER_PITS - 1) ? COUNT_OF_ALL_PITS - 1 : COUNT_OF_PLAYER_PITS - 1;

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
        return (pitId == COUNT_OF_PLAYER_PITS - 1) || (pitId == COUNT_OF_ALL_PITS - 1);
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
        if ((board.getPlayerTurn() == FIRST_PLAYER) && (pitId >= 0 && pitId < COUNT_OF_PLAYER_PITS - 1)) {
            return true;
        }

        // In case of first player the allowed pits are between 7 and 12
        if ((board.getPlayerTurn() == SECOND_PLAYER) && (pitId >= COUNT_OF_PLAYER_PITS && pitId < COUNT_OF_ALL_PITS - 1)) {
            return true;
        }

        return false;
    }
}
