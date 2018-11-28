package com.backbase.kalah.service;

import com.backbase.kalah.error.KalahGameException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.repository.BoardRepository;
import com.google.common.collect.ImmutableList;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;
import static com.backbase.kalah.constant.Constants.PLAYER_1_KALAH;
import static com.backbase.kalah.constant.Constants.PLAYER_2_KALAH;
import static com.backbase.kalah.constant.Messages.GAME_FINISHED_ERROR;
import static com.backbase.kalah.constant.Messages.INVALID_PIT_ID_ERROR;
import static com.backbase.kalah.constant.Messages.KALAH_MOVE_ERROR;
import static com.backbase.kalah.constant.Messages.NOT_PLAYER_TURN_ERROR;
import static com.backbase.kalah.constant.Messages.PIT_EMPTY_ERROR;
import static com.backbase.kalah.constant.Messages.PLAY_AGAIN_MESSAGE;
import static com.backbase.kalah.model.enums.PlayerTurn.PLAYER_1;
import static com.backbase.kalah.model.enums.PlayerTurn.PLAYER_2;
import static com.backbase.kalah.model.enums.Status.FINISHED;
import static com.backbase.kalah.model.enums.Status.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link BoardService}
 *
 * @author Mohamed Morsey
 * Date: 2018-11-27
 **/
@RunWith(MockitoJUnitRunner.class)
public class BoardServiceTest {
    //region field values
    private static final int INVALID_PIT_ID = 20;
    private static final long BOARD_ID = 1L;
    private static final int PIT_0 = 0;
    private static final int PIT_1 = 1;
    private static final int PIT_5 = 5;
    private static final int PIT_10 = 10;
    //endregion

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Mock
    private Logger logger;
    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private Board testBoard;
    private ModelMapper mapper = new ModelMapper();

    @Before
    public void setUp() throws Exception {
        testBoard = boardService.initBoard();
        testBoard.setId(BOARD_ID);
    }

    /**
     * Tests {@link BoardService#get(long)}
     */
    @Test
    public void testGet() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        Optional<Board> existingBoardOptional = boardService.get(BOARD_ID);

        assertThat(existingBoardOptional).isPresent();
        assertThat(existingBoardOptional).hasValueSatisfying(
                board -> {
                    assertThat(board.getId()).isEqualTo(BOARD_ID);
                    assertThat(board.getPlayerTurn()).isEqualTo(PLAYER_1);
                });
    }

    /**
     * Tests {@link BoardService#get(long)} for nonexistent board
     */
    @Test
    public void testGetForNonexistentBoard() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(null);

        Optional<Board> existingBoardOptional = boardService.get(BOARD_ID);

        assertThat(existingBoardOptional).isEmpty();
    }

    /**
     * Tests {@link BoardService#getAll()}
     */
    @Test
    public void testGetAll() {
        when(boardRepository.findAll()).thenReturn(ImmutableList.of(testBoard));

        List<Board> allBoards = boardService.getAll();

        assertThat(allBoards).isNotEmpty();
        assertThat(allBoards).containsExactlyInAnyOrder(testBoard);
    }

    /**
     * Tests {@link BoardService#create(Board)}
     */
    @Test
    public void testCreate() {
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        Board newBoard = boardService.create(testBoard);

        assertThat(newBoard).isNotNull();
        assertThat(newBoard.getId()).isEqualTo(BOARD_ID);
    }

    /**
     * Tests {@link BoardService#update(Board)}
     */
    @Test
    public void testUpdate() {
        Board modifiedBoard = new Board();
        mapper.map(testBoard, modifiedBoard);
        modifiedBoard.setPlayerTurn(PLAYER_2);

        when(boardRepository.exists(BOARD_ID)).thenReturn(true);
        when(boardRepository.save(any(Board.class))).thenReturn(modifiedBoard);

        boolean updateSuccessful = boardService.update(modifiedBoard);

        assertThat(updateSuccessful).isTrue();
        verify(boardRepository).save(modifiedBoard);
    }

    /**
     * Tests {@link BoardService#update(Board)} for nonexistent board
     */
    @Test
    public void testUpdateForNonexistentBoard() {
        Board modifiedBoard = new Board();
        mapper.map(testBoard, modifiedBoard);
        modifiedBoard.setPlayerTurn(PLAYER_2);

        when(boardRepository.exists(BOARD_ID)).thenReturn(false);

        boolean updateSuccessful = boardService.update(modifiedBoard);

        assertThat(updateSuccessful).isFalse();
    }

    /**
     * Tests {@link BoardService#delete(long)}
     */
    @Test
    public void tesDelete() {
        when(boardRepository.exists(BOARD_ID)).thenReturn(true);

        boolean deletionSuccessful = boardService.delete(BOARD_ID);

        assertThat(deletionSuccessful).isTrue();
        verify(boardRepository).delete(BOARD_ID);
    }

    /**
     * Tests {@link BoardService#delete(long)} for nonexistent board
     */
    @Test
    public void tesDeleteForNonexistentBoard() {
        when(boardRepository.exists(BOARD_ID)).thenReturn(false);

        boolean deletionSuccessful = boardService.delete(BOARD_ID);

        assertThat(deletionSuccessful).isFalse();
    }

    /**
     * Tests {@link BoardService#createInitializedBoard()}
     */
    @Test
    public void testCreateInitializedBoard() {
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);

        Board initializedBoard = boardService.createInitializedBoard();

        assertThat(initializedBoard).isNotNull();
        assertThat(initializedBoard.getStatus()).isEqualTo(RUNNING);
        assertThat(initializedBoard.getPlayerTurn()).isEqualTo(PLAYER_1);
        assertThat(initializedBoard.getPits()).hasSize(COUNT_OF_ALL_PITS);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} with player 1 plays only once
     */
    @Test
    public void testMakeMoveWithPlayerTurn() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // In case of pit 1, the last dropped stone won't be in the kalah, so the player turn is switched
        Optional<Board> boardOptional = boardService.makeMove(BOARD_ID, PIT_1);

        assertThat(boardOptional).isNotEmpty();
        assertThat(boardOptional).hasValueSatisfying(
                board -> {
                    assertThat(board.getId()).isEqualTo(BOARD_ID);
                    assertThat(board.getPlayerTurn()).isEqualTo(PLAYER_2);
                    assertThat(board.getPits().get(PIT_1).getStoneCount()).isEqualTo(0);
                });
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} with player 1 plays once more (last stone is dropped into her own Kalah)
     */
    @Test
    public void testMakeMoveWithPlayerPlaysAgain() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // In case of pit 0, the last dropped stone is in the kalah, so player1 plays again
        Optional<Board> boardOptional = boardService.makeMove(BOARD_ID, PIT_0);

        assertThat(boardOptional).isNotEmpty();
        assertThat(boardOptional).hasValueSatisfying(
                board -> {
                    assertThat(board.getId()).isEqualTo(BOARD_ID);
                    assertThat(board.getPlayerTurn()).isEqualTo(PLAYER_1);
                    assertThat(board.getPits().get(PIT_0).getStoneCount()).isEqualTo(0);
                    assertThat(board.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(1);
                });
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} for nonexistent board
     */
    @Test
    public void testMakeMoveForNonexistentBoard() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(null);

        Optional<Board> boardOptional = boardService.makeMove(BOARD_ID, PIT_0);

        assertThat(boardOptional).isEmpty();
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} for an invalid pit ID
     */
    @Test
    public void testMakeMoveForInvalidPitId() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(INVALID_PIT_ID_ERROR);

        boardService.makeMove(BOARD_ID, INVALID_PIT_ID);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} for an-already finished game
     */
    @Test
    public void testMakeMoveForFinishedGame() {
        thrownException.expect(KalahGameException.class);
        thrownException.expectMessage(GAME_FINISHED_ERROR);

        testBoard.setStatus(FINISHED);

        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        boardService.makeMove(BOARD_ID, PIT_1);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} for pit ID refers to a Kalah
     */
    @Test
    public void testMakeMoveWithPitIsKalah() {
        thrownException.expect(KalahGameException.class);
        thrownException.expectMessage(KALAH_MOVE_ERROR);

        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        boardService.makeMove(BOARD_ID, PLAYER_2_KALAH);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but with not player1's turn
     */
    @Test
    public void testMakeMoveWithNotPlayer1Turn() {
        thrownException.expect(KalahGameException.class);
        thrownException.expectMessage(NOT_PLAYER_TURN_ERROR);

        testBoard.setPlayerTurn(PLAYER_2);

        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        boardService.makeMove(BOARD_ID, PIT_0);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but with not player2's turn
     */
    @Test
    public void testMakeMoveWithNotPlayer2Turn() {
        thrownException.expect(KalahGameException.class);
        thrownException.expectMessage(NOT_PLAYER_TURN_ERROR);

        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        boardService.makeMove(BOARD_ID, PIT_10);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but with empty pit
     */
    @Test
    public void testMakeMoveWithEmptyPit() {
        thrownException.expect(KalahGameException.class);
        thrownException.expectMessage(PIT_EMPTY_ERROR + ", " + PLAY_AGAIN_MESSAGE);

        testBoard.getPits().get(PIT_0).setStoneCount(0);

        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);

        boardService.makeMove(BOARD_ID, PIT_0);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but with last dropped stone is in a en empty pit
     */
    @Test
    public void testMakeMoveWithLastPitEmpty() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // Set number of stones to 13, so the last stones comes to the same pit which will be empty at this time
        testBoard.getPits().get(PIT_0).setStoneCount(COUNT_OF_ALL_PITS - 1);

        boardService.makeMove(BOARD_ID, PIT_0);

        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(9);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but for the last move right before the game ends
     * i.e. one of the players has no more stones in all her pits in case player 1 wins
     */
    @Test
    public void testMakeMoveForLastMoveBeforeEndWithPlayer1Wins() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // In order to let the game finish quickly, we set all pits of one player to zero except one
        for(int i = 0; i < PLAYER_1_KALAH; i++){
            testBoard.getPits().get(i).setStoneCount(0);
        }
        testBoard.getPits().get(PIT_0).setStoneCount(1);

        boardService.makeMove(BOARD_ID, PIT_0);

        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(7); //1 + 6 from opposite pit

        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isEqualTo(30); // 5 pits with 6 stones each

        assertThat(testBoard.getStatus()).isEqualTo(FINISHED);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but for the last move right before the game ends
     * i.e. one of the players has no more stones in all her pits in case player 2 wins
     */
    @Test
    public void testMakeMoveForLastMoveBeforeEndWithPlayer2Wins() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // In order to let the game finish quickly, we set all pits of one player to zero except one
        for(int i = PLAYER_1_KALAH + 1; i < PLAYER_2_KALAH; i++){
            testBoard.getPits().get(i).setStoneCount(0);
        }
        testBoard.getPits().get(PIT_10).setStoneCount(1);
        testBoard.setPlayerTurn(PLAYER_2);

        boardService.makeMove(BOARD_ID, PIT_10);

        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(30);// 5 pits with 6 stones each

        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isEqualTo(7);//1 + 6 from opposite pit

        assertThat(testBoard.getStatus()).isEqualTo(FINISHED);
    }

    /**
     * Tests {@link BoardService#makeMove(long, int)} but for the last move right before the game ends
     * i.e. one of the players has no more stones in all her pits in case the game ends in a tie
     */
    @Test
    public void testMakeMoveForLastMoveBeforeEndWithTie() {
        when(boardRepository.findOne(BOARD_ID)).thenReturn(testBoard);
        when(boardRepository.save(testBoard)).thenReturn(testBoard);

        // In order to let the game finish quickly, we set stones of all pits to 0
        for(int i = 0; i < PLAYER_2_KALAH; i++){
            testBoard.getPits().get(i).setStoneCount(0);
        }
        // Set the pit5 to 1, in order to have an available move
        testBoard.getPits().get(PIT_5).setStoneCount(1);
        testBoard.getPits().get(PLAYER_2_KALAH).setStoneCount(1);

        boardService.makeMove(BOARD_ID, PIT_5);

        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(1);

        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isNotZero();
        assertThat(testBoard.getPits().get(PLAYER_2_KALAH).getStoneCount()).isEqualTo(1);

        assertThat(testBoard.getStatus()).isEqualTo(FINISHED);
    }
}