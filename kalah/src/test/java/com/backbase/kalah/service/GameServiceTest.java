package com.backbase.kalah.service;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.GameRepository;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
/**
 * Test class for {@link GameService}
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {
    //region field values
    private static final long GAME_ID = 1L;
    private static final String GAME_URI = "http://example.org/games/1";
    //endregion

    @Mock private Logger logger;
    @Mock private GameRepository gameRepository;
    @Mock private BoardService boardService;

    @InjectMocks private GameService gameService;

    private Game testGame;

    @Before
    public void setUp() throws Exception {
        Board emptyBoard = new Board();
        testGame = new Game(emptyBoard, GAME_URI);
        testGame.setId(GAME_ID);
    }

    @Test
    public void createNewGame() {
        //gameService.createNewGame();
    }

    /**
     * Tests {@link GameService#get(long)}
     */
    @Test
    public void testGet() {
        when(gameRepository.findOne(GAME_ID)).thenReturn(testGame);

        Optional<Game> existingGameOptional = gameService.get(GAME_ID);

        assertThat(existingGameOptional).isPresent();
        assertThat(existingGameOptional).hasValueSatisfying(
                game -> {
                    assertThat(game.getId()).isEqualTo(GAME_ID);
                    assertThat(game.getUri()).isEqualTo(GAME_URI);
                });
    }

    @Test
    public void getAll() {
    }

    @Test
    public void create() {
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void exists() {
    }

    @Test
    public void createNewGame1() {
    }

    @Test
    public void makeMove() {
    }
}