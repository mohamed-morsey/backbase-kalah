package com.backbase.kalah.rest;

import com.backbase.kalah.model.Game;
import com.backbase.kalah.service.BoardService;
import com.backbase.kalah.service.GameService;
import com.backbase.kalah.view.GameView;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

import static com.backbase.kalah.constant.Fields.ID_PARAMETER;
import static com.backbase.kalah.constant.Fields.PIT_ID_PARAMETER;
import static com.backbase.kalah.constant.Messages.GAME_CREATION_FAILED_ERROR;
import static com.backbase.kalah.constant.Messages.GAME_NOT_FOUND_ERROR;
import static com.backbase.kalah.constant.Messages.INVALID_ID_ERROR;
import static com.backbase.kalah.constant.Messages.INVALID_PIT_ID_ERROR;
import static com.backbase.kalah.constant.Paths.GAMES_CONTEXT_PATH;
import static com.backbase.kalah.constant.Paths.PITS_CONTEXT_PATH;

/**
 * Controller for {@link Game}s
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
@RestController
@RequestMapping("/" + GAMES_CONTEXT_PATH)
public class GameRestController {
    private GameService gameService;
    private BoardService boardService;
    private Logger logger;

    @Inject
    public GameRestController(GameService gameService, Logger logger, BoardService boardService) {
        this.gameService = gameService;
        this.logger = logger;
        this.boardService = boardService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameView> createGame() {
        Optional<Game> gameOptional = gameService.createNewGame();

        if (!gameOptional.isPresent()) {
            logger.warn(GAME_CREATION_FAILED_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Game newGame = gameOptional.get();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(gameOptional.get().getId())
                .toUri();

        GameView view = new GameView(newGame.getId(), location.toString());

        return ResponseEntity.created(location).body(view);
    }

    @PutMapping(path = "/{id}/" + PITS_CONTEXT_PATH + "/{pitId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity makeMove(@PathVariable(ID_PARAMETER) final String id,
                                   @PathVariable(PIT_ID_PARAMETER) final String pitId) {
        // Check if a valid game ID is passed
        if ((StringUtils.isBlank(id)) || (!StringUtils.isNumeric(id))) {
            logger.warn(INVALID_ID_ERROR);
            throw new IllegalArgumentException(INVALID_ID_ERROR);
        }

        // Check if a valid pit ID is passed
        if ((StringUtils.isBlank(pitId)) || (!StringUtils.isNumeric(pitId))) {
            logger.warn(INVALID_PIT_ID_ERROR);
            throw new IllegalArgumentException(INVALID_PIT_ID_ERROR);
        }

        long idLong = Long.parseLong(id);
        int pitIdLong = Integer.parseInt(pitId);

        if (!gameService.exists(idLong)) {
            logger.warn(GAME_NOT_FOUND_ERROR);
            return ResponseEntity.notFound().build();
        }

        boardService.makeMove(idLong, pitIdLong);

        return ResponseEntity.ok().build();
    }

}
