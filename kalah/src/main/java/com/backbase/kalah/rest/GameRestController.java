package com.backbase.kalah.rest;

import com.backbase.kalah.dto.GameDto;
import com.backbase.kalah.dto.GameStatusDto;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.service.GameService;
import com.backbase.kalah.utils.GameStatusDtoConverter;
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
    private Logger logger;

    @Inject
    public GameRestController(GameService gameService, Logger logger) {
        this.gameService = gameService;
        this.logger = logger;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameDto> createGame() {
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
        newGame.setUri(location.toString());
        gameService.update(newGame); // Update the new game with the correct URI

        GameDto gameDto = new GameDto(newGame.getId(), newGame.getUri());

        return ResponseEntity.created(location).body(gameDto);
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

        // pitId - 1 as it should be zero based but in the interface is it's one based
        Optional<Game> gameOptional = gameService.makeMove(idLong, pitIdLong - 1);

        // In case the game is not found in the system gameOptional will be Optional#EMPTY
        if (!gameOptional.isPresent()) {
            logger.warn(GAME_NOT_FOUND_ERROR);
            return ResponseEntity.notFound().build();
        }

        GameStatusDto dto = GameStatusDtoConverter.toGameStatusDto(gameOptional.get());

        return ResponseEntity.ok().body(dto);
    }

}
