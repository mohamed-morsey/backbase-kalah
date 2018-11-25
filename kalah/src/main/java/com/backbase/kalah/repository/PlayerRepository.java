package com.backbase.kalah.repository;

import com.backbase.kalah.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Player}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findById(long id);

    boolean existsById(long id);

    Player findByEmail(String email);

    boolean existsByEmail(String email);
}
