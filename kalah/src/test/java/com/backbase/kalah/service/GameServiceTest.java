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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private static final String BASE_URI = "http://example.org/games";
    //endregion

    @Mock
    private Logger logger;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private BoardService boardService;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private ModelMapper mapper = new ModelMapper();

    @Before
    public void setUp() throws Exception {
        Board emptyBoard = new Board();
        testGame = new Game(emptyBoard, GAME_URI);
        testGame.setId(GAME_ID);
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
        assertThat(allGames).containsExactlyInAnyOrder(testGame);
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

    /**
     * Tests {@link GameService#delete(long)}
     */
    @Test
    public void tesDelete() {
        when(gameRepository.exists(GAME_ID)).thenReturn(true);

        boolean deletionSuccessful = gameService.delete(GAME_ID);

        assertThat(deletionSuccessful).isTrue();
        verify(gameRepository).delete(GAME_ID);
    }

    /**
     * Tests {@link GameService#delete(long)} for nonexistent game
     */
    @Test
    public void tesDeleteForNonexistentGame() {
        when(gameRepository.exists(GAME_ID)).thenReturn(false);

        boolean deletionSuccessful = gameService.delete(GAME_ID);

        assertThat(deletionSuccessful).isFalse();
    }

    /**
     * Tests {@link GameService#exists(long)}
     */
    @Test
    public void testExists() {
        when(gameRepository.exists(GAME_ID)).thenReturn(true);

        boolean exists = gameService.exists(GAME_ID);

        assertThat(exists).isTrue();
    }

    /**
     * Tests {@link GameService#exists(long)} for nonexistent game
     */
    @Test
    public void testExistsForNonexistentGame() {
        when(gameRepository.exists(GAME_ID)).thenReturn(false);

        boolean exists = gameService.exists(GAME_ID);

        assertThat(exists).isFalse();
    }

    /**
     * Tests {@link GameService#createNewGame(String)}
     */
    @Test
    public void testCreateNewGame() {
        when(boardService.createInitializedBoard()).thenReturn(new Board());
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Optional<Game> newGameOptional = gameService.createNewGame(BASE_URI);

        assertThat(newGameOptional).isPresent();
        assertThat(newGameOptional).hasValueSatisfying(
                game -> {
                    assertThat(game.getId()).isEqualTo(GAME_ID);
                });
    }

    /**
     * Tests {@link GameService#makeMove(long, int)}
     */
    @Test
    public void testMakeMove() {
        when(gameRepository.findOne(GAME_ID)).thenReturn(testGame);

        Optional<Game> gameOptional = gameService.makeMove(GAME_ID, 1);

        assertThat(gameOptional).isPresent();
        assertThat(gameOptional).hasValueSatisfying(
                game -> {
                    assertThat(game.getId()).isEqualTo(GAME_ID);
                });
    }

    /**
     * Tests {@link GameService#makeMove(long, int)} for nonexistent game
     */
    @Test
    public void testMakeMoveForNonexistentGame() {
        when(gameRepository.findOne(GAME_ID)).thenReturn(null);

        Optional<Game> gameOptional = gameService.makeMove(GAME_ID, 1);

        assertThat(gameOptional).isEmpty();
    }

    /**
     * Tests {@link GameService#makeMove(long, int)} for negative pit ID
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMakeMoveForNegativePitId() {
        gameService.makeMove(GAME_ID, -1);
    }

    /**
     * Tests {@link GameService#makeMove(long, int)} for large pit ID exceeding the number of allowed pits
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMakeMoveForLargePitId() {
        gameService.makeMove(GAME_ID, 20);
    }
}