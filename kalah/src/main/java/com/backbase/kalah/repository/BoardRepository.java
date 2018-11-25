package com.backbase.kalah.repository;

import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link Board}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findById(long id);

    boolean existsById(long id);
}
