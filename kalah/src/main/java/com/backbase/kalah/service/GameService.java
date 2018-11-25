package com.backbase.kalah.service;

import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.GameRepository;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

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

    public Optional<Game> createNewGame() {
        Game newGame = new Game();
        newGame.setBoard(boardService.createInitializedBoard());

        return Optional.of(gameRepository.save(newGame));
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

        if (!gameRepository.exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        gameRepository.save(item);
        return true;
    }

    @Override
    public boolean delete(long id) {
        if (!gameRepository.exists(id)) {
            logger.warn(ITEM_NOT_FOUND_ERROR);
            return false;
        }

        gameRepository.delete(id);
        return true;
    }
}
