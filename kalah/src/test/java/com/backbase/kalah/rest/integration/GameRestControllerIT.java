package com.backbase.kalah.rest.integration;

import com.backbase.kalah.dto.GameDto;
import com.backbase.kalah.dto.GameStatusDto;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.BoardRepository;
import com.backbase.kalah.repository.GameRepository;
import com.backbase.kalah.repository.PitRepository;
import com.backbase.kalah.rest.GameRestController;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.backbase.kalah.constant.Constants.PLAYER_1_KALAH;
import static com.backbase.kalah.constant.Constants.PLAYER_2_KALAH;
import static com.backbase.kalah.constant.Fields.PIT_ID_PARAMETER;
import static com.backbase.kalah.constant.Paths.GAMES_CONTEXT_PATH;
import static com.backbase.kalah.constant.Paths.PITS_CONTEXT_PATH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

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
            new ParameterizedTypeReference<GameDto>() {
            };
    private static final ParameterizedTypeReference<GameStatusDto> GAME_STATUS_DTO_RESPONSE_TYPE =
            new ParameterizedTypeReference<GameStatusDto>() {
            };
    private static final ParameterizedTypeReference<DefaultErrorAttributes> DEFAULT_ERROR_ATTRIBUTES_RESPONSE_TYPE =
            new ParameterizedTypeReference<DefaultErrorAttributes>() {
            };

    //region constants
    private static final String PLAYER1_WINNING_SEQUENCE_FILENAME = "player1-winning-sequence";
    private static final String PLAYER2_WINNING_SEQUENCE_FILENAME = "player2-winning-sequence";
    private static final String PIT_1_ID = "1";
    //endregion

    @Inject
    private PitRepository pitRepository;
    @Inject
    private BoardRepository boardRepository;
    @Inject
    private GameRepository gameRepository;
    @Inject
    private TestRestTemplate restTemplate;

    private Game testGame;
    private Board testBoard;

    private UriComponentsBuilder builder;


    @After
    public void teardown() {
        gameRepository.deleteAll();
        boardRepository.deleteAll();
        pitRepository.deleteAll();
    }

    /**
     * Tests the following scenario
     * <ul>
     * <li>Create a game</li>
     * <li>Play a sequence of moves</li>
     * <li>End result is that player1 wins</li>
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

        ResponseEntity<GameDto> gameCreationResult =
                restTemplate.exchange(uri, POST, HttpEntity.EMPTY, GAME_DTO_RESPONSE_TYPE);

        // Check that the game has been created successfully
        assertThat(gameCreationResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
        assertThat(gameCreationResult.getBody()).isNotNull();
        assertThat(gameCreationResult.getHeaders().get(HttpHeaders.LOCATION).get(0))
                .isEqualTo(gameCreationResult.getBody().getUri());

        String gameUri = gameCreationResult.getBody().getUri();

        String uriTemplate = gameUri + "/" + PITS_CONTEXT_PATH + "/{" + PIT_ID_PARAMETER + "}";
        Map<String, String> uriParams = new HashMap<>();
        builder = UriComponentsBuilder.fromUriString(uriTemplate);
        ResponseEntity<GameStatusDto> gamePlayResult = null;

        for (String currentPitIndex : playSequence) {
            uriParams.put(PIT_ID_PARAMETER, currentPitIndex);
            uri = builder.buildAndExpand(uriParams).toUriString();

            gamePlayResult = restTemplate.exchange(uri, PUT, HttpEntity.EMPTY, GAME_STATUS_DTO_RESPONSE_TYPE);
        }

        // Check that player1 has higher score
        assertThat(gamePlayResult).isNotNull();
        assertThat(gamePlayResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_OK);

        // Get scores of both players
        String player1Score = gamePlayResult.getBody().getStatus().get(String.valueOf(PLAYER_1_KALAH + 1));
        String player2Score = gamePlayResult.getBody().getStatus().get(String.valueOf(PLAYER_2_KALAH + 1));

        assertThat(Integer.parseInt(player1Score)).isGreaterThan(Integer.parseInt(player2Score));

        // Now, the game has ended, if we try to do any other move, we should get an error, stating that the game is finished
        uriParams.put(PIT_ID_PARAMETER, PIT_1_ID);
        uri = builder.buildAndExpand(uriParams).toUriString();

        ResponseEntity<DefaultErrorAttributes> gameErrorResult = restTemplate.exchange(uri, PUT, HttpEntity.EMPTY, DEFAULT_ERROR_ATTRIBUTES_RESPONSE_TYPE);

        assertThat(gameErrorResult).isNotNull();
        assertThat(gameErrorResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Tests the following scenario
     * <ul>
     * <li>Create a game</li>
     * <li>Play a sequence of moves</li>
     * <li>End result is that player2 wins</li>
     * </ul>
     */
    @Test
    public void testCreateAndPlayGameWithPlayer2Wins() throws Exception {
        String player1WinningSequence = IOUtils.toString(
                this.getClass().getResourceAsStream(PLAYER2_WINNING_SEQUENCE_FILENAME), UTF_8);
        String[] playSequence = player1WinningSequence.split("\n");

        // Prepare the correct URI to contact the game REST controller and create the game
        builder = UriComponentsBuilder.fromUriString("/" + GAMES_CONTEXT_PATH);
        String uri = builder.toUriString();

        ResponseEntity<GameDto> gameCreationResult =
                restTemplate.exchange(uri, POST, HttpEntity.EMPTY, GAME_DTO_RESPONSE_TYPE);

        // Check that the game has been created successfully
        assertThat(gameCreationResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_CREATED);
        assertThat(gameCreationResult.getBody()).isNotNull();
        assertThat(gameCreationResult.getHeaders().get(HttpHeaders.LOCATION).get(0))
                .isEqualTo(gameCreationResult.getBody().getUri());

        String gameUri = gameCreationResult.getBody().getUri();

        String uriTemplate = gameUri + "/" + PITS_CONTEXT_PATH + "/{" + PIT_ID_PARAMETER + "}";
        Map<String, String> uriParams = new HashMap<>();
        builder = UriComponentsBuilder.fromUriString(uriTemplate);
        ResponseEntity<GameStatusDto> gamePlayResult = null;

        for (String currentPitIndex : playSequence) {
            uriParams.put(PIT_ID_PARAMETER, currentPitIndex);
            uri = builder.buildAndExpand(uriParams).toUriString();

            gamePlayResult = restTemplate.exchange(uri, PUT, HttpEntity.EMPTY, GAME_STATUS_DTO_RESPONSE_TYPE);
        }

        // Check that player1 has higher score
        assertThat(gamePlayResult).isNotNull();
        assertThat(gamePlayResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_OK);

        // Get scores of both players
        String player1Score = gamePlayResult.getBody().getStatus().get(String.valueOf(PLAYER_1_KALAH + 1));
        String player2Score = gamePlayResult.getBody().getStatus().get(String.valueOf(PLAYER_2_KALAH + 1));

        assertThat(Integer.parseInt(player1Score)).isLessThan(Integer.parseInt(player2Score));

        // Now, the game has ended, if we try to do any other move, we should get an error
        uriParams.put(PIT_ID_PARAMETER, PIT_1_ID);
        uri = builder.buildAndExpand(uriParams).toUriString();

        ResponseEntity<DefaultErrorAttributes> gameErrorResult = restTemplate.exchange(uri, PUT, HttpEntity.EMPTY, DEFAULT_ERROR_ATTRIBUTES_RESPONSE_TYPE);

        assertThat(gameErrorResult).isNotNull();
        assertThat(gameErrorResult.getStatusCode().value()).isEqualTo(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}