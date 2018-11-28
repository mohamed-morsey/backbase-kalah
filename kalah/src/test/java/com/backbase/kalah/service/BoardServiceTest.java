package com.backbase.kalah.service;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.repository.BoardRepository;
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

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;
import static com.backbase.kalah.model.enums.PlayerTurn.FIRST_PLAYER;
import static com.backbase.kalah.model.enums.PlayerTurn.SECOND_PLAYER;
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
    private static final long BOARD_ID = 1L;
    private static final int PIT_0 = 0;
    private static final int PIT_1 = 1;
    private static final int PLAYER_1_KALAH = 6;
    private static final int PLAYER_2_KALAH = 13;
    //endregion

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
                    assertThat(board.getPlayerTurn()).isEqualTo(FIRST_PLAYER);
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
        modifiedBoard.setPlayerTurn(SECOND_PLAYER);

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
        modifiedBoard.setPlayerTurn(SECOND_PLAYER);

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
        assertThat(initializedBoard.getPlayerTurn()).isEqualTo(FIRST_PLAYER);
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
                    assertThat(board.getPlayerTurn()).isEqualTo(SECOND_PLAYER);
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

        // In case of pit 1, the last dropped stone won't be in the kalah, so the player turn is switched
        Optional<Board> boardOptional = boardService.makeMove(BOARD_ID, PIT_0);

        assertThat(boardOptional).isNotEmpty();
        assertThat(boardOptional).hasValueSatisfying(
                board -> {
                    assertThat(board.getId()).isEqualTo(BOARD_ID);
                    assertThat(board.getPlayerTurn()).isEqualTo(FIRST_PLAYER);
                    assertThat(board.getPits().get(PIT_0).getStoneCount()).isEqualTo(0);
                    assertThat(board.getPits().get(PLAYER_1_KALAH).getStoneCount()).isEqualTo(1);
                });
    }
}