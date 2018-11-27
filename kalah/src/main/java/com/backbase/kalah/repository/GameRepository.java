package com.backbase.kalah.repository;

import com.backbase.kalah.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Game}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public interface GameRepository extends JpaRepository<Game, Long> {
}
