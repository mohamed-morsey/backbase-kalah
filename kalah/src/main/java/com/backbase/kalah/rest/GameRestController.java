package com.backbase.kalah.rest;

import com.backbase.kalah.model.Game;
import com.backbase.kalah.service.GameService;
import com.backbase.kalah.view.GameView;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.inject.Inject;
import java.net.URI;
import java.util.Optional;

import static com.backbase.kalah.constant.Messages.GAME_CREATION_FAILED_ERROR;
import static com.backbase.kalah.constant.Paths.GAMES_CONTEXT_PTAH;

/**
 * Controller for {@link Game}s
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
@RestController
@RequestMapping("/" + GAMES_CONTEXT_PTAH)
public class GameRestController {
    private GameService gameService;
    private Logger logger;

    @Inject
    public GameRestController(GameService gameService, Logger logger) {
        this.gameService = gameService;
        this.logger = logger;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameView> createGame(Model model) {
        Optional<Game> gameOptional = gameService.createNewGame();

        if(!gameOptional.isPresent()){
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

}
