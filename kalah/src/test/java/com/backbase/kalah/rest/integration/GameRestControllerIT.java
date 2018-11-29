package com.backbase.kalah.rest.integration;

import com.backbase.kalah.dto.GameDto;
import com.backbase.kalah.dto.GameStatusDto;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.BoardRepository;
import com.backbase.kalah.repository.GameRepository;
import com.backbase.kalah.repository.PitRepository;
import com.backbase.kalah.rest.GameRestController;
import com.backbase.kalah.service.BoardService;
import com.backbase.kalah.service.GameService;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static com.backbase.kalah.constant.Fields.PIT_ID_PARAMETER;
import static com.backbase.kalah.constant.Paths.GAMES_CONTEXT_PATH;
import static com.backbase.kalah.constant.Paths.PITS_CONTEXT_PATH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.apache.commons.io.IOUtils;

/**
 * Integration test class for {@link GameRestController}
 * that tests the whole game play scenario
 *
 * @author Mohamed Morsey
 * Date: 2018-11-29
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameRestControllerIT {

    private static final ParameterizedTypeReference<GameDto> GAME_DTO_RESPONSE_TYPE =
            new ParameterizedTypeReference<GameDto>() {};
    private static final ParameterizedTypeReference<GameStatusDto> GAME_STATUS_DTO_RESPONSE_TYPE =
            new ParameterizedTypeReference<GameStatusDto>() {};

    //region constants
    private static final String PLAYER1_WINNING_SEQUENCE_FILENAME = "player1-winning-sequence";
    private static final String PLAYER2_WINNING_SEQUENCE_FILENAME = "player2-winning-sequence";
    //endregion

    @Inject
    private PitRepository pitRepository;
    @Inject
    private BoardRepository boardRepository;
    @Inject
    private GameRepository gameRepository;
    @Inject
    private BoardService boardService;
    @Inject
    private GameService gameService;
    @Inject
    private TestRestTemplate restTemplate;
    @Inject
    private GameRestController gameRestController;

    private Game testGame;
    private Board testBoard;

    private UriComponentsBuilder builder;

    @Before
    public void setUp() throws Exception {
//        testBoard = boardService.initBoard();
//        testBoard.setId(BOARD_ID);
//
//        testGame = new Game(testBoard, GAME_URI);
//        testGame.setId(GAME_ID);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(gameRestController)
//                .setControllerAdvice(new KalahGameExceptionHandler()).build();
    }

    @After
    public void teardown() {
        gameRepository.deleteAll();
        boardRepository.deleteAll();
        pitRepository.deleteAll();
    }

    /**
     * Tests the following scenario
     * <ul>
     * <li>Get number of customers, accounts and transactions in the system</li>
     * <li>Add customer</li>
     * <li>Get number of customers after add</li>
     * <li>Get number of accounts</li>
     * <li>Add account</li>
     * <li>Get number of accounts after add</li>
     * <li>Get number of transactions after add</li>
     * </ul>
     */
    @Test
    public void testCreateAndPlayGameWithPlayer1Wins() throws Exception {
        String player1WinningSequence = IOUtils.toString(
                this.getClass().getResourceAsStream(PLAYER1_WINNING_SEQUENCE_FILENAME), UTF_8);
        String[] playSequence = player1WinningSequence.split("\n");

        // Prepare the correct URI to contact the game REST controller and create the game
        builder = UriComponentsBuilder.fromUriString("/" + GAMES_CONTEXT_PATH);
        String uri = builder.toUriString();

//        MvcResult gameCreationResult = this.mockMvc.perform(post(uri))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$." + ID_FIELD).value(GAME_ID)).andReturn();
//
//        String content = gameCreationResult.getResponse().get

        ResponseEntity<GameDto> gameCreationResult =
                restTemplate.exchange(uri, POST, HttpEntity.EMPTY, GAME_DTO_RESPONSE_TYPE);
        String gameUri = gameCreationResult.getBody().getUri();


        String uriTemplate = gameUri + "/" + PITS_CONTEXT_PATH + "/{pitId}";
        Map<String, String> uriParams = new HashMap<>();
        builder = UriComponentsBuilder.fromUriString(uriTemplate);

        for (String pitPath:playSequence) {
            uriParams.put(PIT_ID_PARAMETER, pitPath);
            uri = builder.buildAndExpand(uriParams).toUriString();

            ResponseEntity<GameStatusDto> gamePlayResult =
                    restTemplate.exchange(uri, PUT, HttpEntity.EMPTY, GAME_STATUS_DTO_RESPONSE_TYPE);

            System.out.println(gamePlayResult.getBody());
        }

    }
}