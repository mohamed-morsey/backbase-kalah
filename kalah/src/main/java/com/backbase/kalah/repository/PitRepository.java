package com.backbase.kalah.repository;

import com.backbase.kalah.model.Pit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Pit}s
 *
 * @author Mohamed Morsey
 * Date: 2018-11-29
 **/
public interface PitRepository extends JpaRepository<Pit, Long> {
}
