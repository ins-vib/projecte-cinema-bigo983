package com.daw.cinemadaw.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Screening;


@Repository
public interface  ScreeningRepository extends JpaRepository<Screening, Long>{
    List<Screening> findByMovieId(Long movieId);


    List<Screening> findBYMovieid(Long movieId);

    List<Screening> findByMovieAndScreeningDatTimeGReaterThanEqualOrderBYScreeningDatTimeAsc(Movie movie, LocalDateTime dateTime);
}
