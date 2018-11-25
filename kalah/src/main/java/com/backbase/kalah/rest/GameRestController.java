package com.backbase.kalah.rest;

import com.backbase.kalah.model.Game;
import com.backbase.kalah.service.GameService;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Controller for {@link Game}s
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
@RestController
@RequestMapping("/")
public class GameRestController {

    private GameService gameService;

    private ModelMapper mapper = new ModelMapper(); // Mapper for converting between entities and DTOs

    @Inject
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public String init(Model model) {
        gameService.createNewGame();
        return "";
    }


}
