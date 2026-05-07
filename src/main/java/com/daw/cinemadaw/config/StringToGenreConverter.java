package com.daw.cinemadaw.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.daw.cinemadaw.domain.cinema.Genre;
import com.daw.cinemadaw.repository.GenreRepository;

// Converter global que tradueix l'ID d'un gènere (vingut com a String del
// formulari, p. ex. "3") a l'entitat Genre carregada de la base de dades.
//
// Sense aquest bean Spring no pot fer el binding automàtic dels checkboxes
// del formulari de pel·lícules cap a Set<Genre> dins de Movie.
//
// En registrar-lo com a @Component, Spring l'afegeix automàticament al
// ConversionService global i s'aplica a qualsevol controller.
@Component
public class StringToGenreConverter implements Converter<String, Genre> {

    private final GenreRepository genreRepository;

    public StringToGenreConverter(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre convert(String source) {
        // Cadena buida → res seleccionat
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(source);
            return genreRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            // ID no vàlid → ignorem (la validació @NotEmpty avisarà si cal)
            return null;
        }
    }
}
