package com.backbase.kalah.rest;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.service.KalahGameService;
import org.modelmapper.ModelMapper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * Controller for {@link Board}s
 *
 * @author Mohamed Morsey
 * Date: 2018-10-05
 **/
@RestController
@RequestMapping("/")
public class BoardRestController {

    private KalahGameService kalahGameService;

    private ModelMapper mapper = new ModelMapper(); // Mapper for converting between entities and DTOs

    @Inject
    public BoardRestController(KalahGameService kalahGameService) {
        this.kalahGameService = kalahGameService;
    }

    @GetMapping
    public String init(Model model) {
        kalahGameService.createNewGame();
        return "";
    }


}
