package com.backbase.kalah.service;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import com.backbase.kalah.repository.GameRepository;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
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
    private static final String MODIFIED_GAME_URI = "http://example.org/games/123";
    //endregion

    @Mock private Logger logger;
    @Mock private GameRepository gameRepository;
    @Mock private BoardService boardService;

    @InjectMocks private GameService gameService;

    private Game testGame;
    private ModelMapper mapper = new ModelMapper();

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

    /**
     * Tests {@link GameService#get(long)} for nonexistent game
     */
    @Test
    public void testGetForNonexistentGame() {

        when(gameRepository.findOne(GAME_ID)).thenReturn(null);

        Optional<Game> existingGameOptional = gameService.get(GAME_ID);

        assertThat(existingGameOptional).isEmpty();
    }

    /**
     * Tests {@link GameService#getAll()}
     */
    @Test
    public void testGetAll() {
        when(gameRepository.findAll()).thenReturn(ImmutableList.of(testGame));

        List<Game> allGames = gameService.getAll();

        assertThat(allGames).isNotEmpty();
        assertThat(allGames).containsExactly(testGame);
    }

    /**
     * Tests {@link GameService#create(Game)}
     */
    @Test
    public void testCreate() {
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Game newGame = gameService.create(testGame);

        assertThat(newGame).isNotNull();
        assertThat(newGame.getId()).isEqualTo(GAME_ID);
    }

    /**
     * Tests {@link GameService#update(Game)}
     */
    @Test
    public void testUpdate() {
        Game modifiedGame = new Game();
        mapper.map(testGame, modifiedGame);
        modifiedGame.setUri(MODIFIED_GAME_URI);

        when(gameRepository.exists(GAME_ID)).thenReturn(true);
        when(gameRepository.save(any(Game.class))).thenReturn(modifiedGame);

        boolean updateSuccessful = gameService.update(modifiedGame);

        assertThat(updateSuccessful).isTrue();
        verify(gameRepository).save(modifiedGame);
    }

    /**
     * Tests {@link GameService#update(Game)} for nonexistent game
     */
    @Test
    public void testUpdateForNonexistentGame() {
        Game modifiedGame = new Game();
        mapper.map(testGame, modifiedGame);
        modifiedGame.setUri(MODIFIED_GAME_URI);

        when(gameRepository.exists(GAME_ID)).thenReturn(false);

        boolean updateSuccessful = gameService.update(modifiedGame);

        assertThat(updateSuccessful).isFalse();
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