package com.daw.cinemadaw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.daw.cinemadaw.domain.cinema.Movie;

@Repository

public interface  MovieRepository extends JpaRepository<Movie, Long> {
    
    List<Movie> findByGenre(String genre);

    @Query("""
            
    SELECT DISTINCT s.movie
    FROM Screening s
    WHERE s.dateTime >= CURRENT_TIMESTAMP
    """)
    List<Movie> findMoviesWithFutureScreenings();
}
