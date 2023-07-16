package com.example.TheGrid.repository;

import com.example.TheGrid.dto.GridMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface MessageRepository extends JpaRepository<GridMessage, Long> {

    @Query("select m.grid, count(m) from GridMessage m " +
            "where m.grid in :grids and m.createAt > :time group by m.grid")
    List<Object[]> getAllMessageCount(@Param("grids") List<String> grids, @Param("time") LocalDateTime time);

    @Query("select m from GridMessage m where m.grid =:grid and m.createAt > :time")
    List<GridMessage> findAllByGrid(@Param("grid") String grid, @Param("time") LocalDateTime time);


}
