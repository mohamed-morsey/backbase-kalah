package com.backbase.kalah.repository;

import com.backbase.kalah.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Board}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public interface BoardRepository extends JpaRepository<Board, Long> {
}
