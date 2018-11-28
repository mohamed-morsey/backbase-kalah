package com.backbase.kalah.rest;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.service.GameService;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.backbase.kalah.constant.Fields.ID_FIELD;
import static com.backbase.kalah.constant.Fields.ID_PARAMETER;
import static com.backbase.kalah.constant.Fields.PIT_ID_PARAMETER;
import static com.backbase.kalah.constant.Messages.GAME_CREATION_FAILED_ERROR;
import static com.backbase.kalah.constant.Paths.GAMES_CONTEXT_PATH;
import static com.backbase.kalah.constant.Paths.PITS_CONTEXT_PATH;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for {@link GameRestController}
 *
 * @author Mohamed Morsey
 * Date: 2018-11-28
 **/
@RunWith(MockitoJUnitRunner.class)
public class GameRestControllerTest {
    //region field values
    private static final long GAME_ID = 1L;
    private static final int PIT_ID = 1;
    private static final String GAME_URI = "http://example.org/games/1";
    private static final String BASE_URI = "/" + GAMES_CONTEXT_PATH;
    //endregion

    @Mock
    private GameService gameService;
    @Mock
    private Logger logger;

    @InjectMocks
    private GameRestController gameRestController;

    private MockMvc mockMvc;
    private ModelMapper mapper = new ModelMapper(); // Mapper for converting between entities and DTOs
    private UriComponentsBuilder builder;

    private Game testGame;
    private Board testBoard;

    @Before
    public void setUp() throws Exception {
        testBoard = new Board();
        testGame = new Game(testBoard, BASE_URI);
        testGame.setId(GAME_ID);

        builder = UriComponentsBuilder.fromUriString(BASE_URI);

        this.mockMvc = MockMvcBuilders.standaloneSetup(gameRestController).build();
    }

    /**
     * Tests {@link GameRestController#createGame()}
     * @throws Exception
     */
    @Test
    public void testCreateGame() throws Exception {
        when(gameService.createNewGame(anyString())).thenReturn(Optional.of(testGame));

        this.mockMvc
                .perform(post(builder.build().toUri()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$." + ID_FIELD).value(GAME_ID));
    }

    /**
     * Tests {@link GameRestController#createGame()} with creation failure
     * @throws Exception
     */
    @Test
    public void testCreateGameWithCreationFailure() throws Exception {
        when(gameService.createNewGame(anyString())).thenReturn(Optional.empty());

        this.mockMvc
                .perform(post(builder.build().toUri()))
                .andExpect(status().isInternalServerError());

        verify(logger).warn(GAME_CREATION_FAILED_ERROR);
    }

    /**
     * Tests {@link GameRestController#makeMove(String, String)}
     * @throws Exception
     */
    @Test
    public void testMakeMove() throws Exception {
        URI uri = builder.path("/{id}/" + PITS_CONTEXT_PATH + "/{pitId}").buildAndExpand(String.valueOf(GAME_ID), String.valueOf(PIT_ID)).toUri();

        this.mockMvc
                .perform(put(uri))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getGameScore() {
    }
}