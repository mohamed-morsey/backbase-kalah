package com.backbase.kalah.service;

import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.GameRepository;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;
import static com.backbase.kalah.constant.Messages.GAME_NOT_FOUND_ERROR;
import static com.backbase.kalah.constant.Messages.INVALID_PIT_ID_ERROR;
import static com.backbase.kalah.constant.Messages.ITEM_NOT_FOUND_ERROR;

/**
 * A service for managing Kalah {@link Game}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Service
public class GameService implements CrudService<Game> {
    private Logger logger;
    private GameRepository gameRepository;
    private BoardService boardService;

    @Inject
    public GameService(GameRepository gameRepository, BoardService boardService, Logger logger) {
        this.gameRepository = gameRepository;
        this.boardService = boardService;
        this.logger = logger;
    }

    @Override
    public Optional<Game> get(long id) {
        return Optional.of(gameRepository.findOne(id));
    }

    @Override
    public List<Game> getAll() {
        return ImmutableList.copyOf(gameRepository.findAll());
    }

    @Override
    public Game create(Game item) {
        return gameRepository.save(item);
    }

    @Override
    public boolean update(Game item) {
        long id = item.getId();

        if (!exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        gameRepository.save(item);
        return true;
    }

    @Override
    public boolean delete(long id) {
        if (!exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        gameRepository.delete(id);
        return true;
    }

    public boolean exists(long id) {
        return gameRepository.exists(id);
    }

    public Optional<Game> createNewGame() {
        Game newGame = new Game();
        newGame.setBoard(boardService.createInitializedBoard());

        return Optional.of(gameRepository.save(newGame));
    }

    /**
     * Makes a move on the given game
     *
     * @param id    The ID of the game
     * @param pitId The ID of the pit to be used for making the move
     * @return The status of the game after making move if successful, {@link Optional#EMPTY} otherwise
     */
    public Optional<Game> makeMove(long id, int pitId) {
        Preconditions.checkArgument(pitId >= 0 && pitId < COUNT_OF_ALL_PITS, INVALID_PIT_ID_ERROR);

        Optional<Game> gameOptional = get(id);
        if (!gameOptional.isPresent()) {
            logger.warn(GAME_NOT_FOUND_ERROR);
            return Optional.empty();
        }

        Game desiredGame = gameOptional.get();
        boardService.makeMove(desiredGame.getBoard().getId(), pitId);
        return Optional.of(desiredGame);
    }

}
