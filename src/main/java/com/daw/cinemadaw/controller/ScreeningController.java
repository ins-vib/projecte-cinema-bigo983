package com.daw.cinemadaw.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daw.cinemadaw.domain.cinema.Movie;
import com.daw.cinemadaw.domain.cinema.Room;
import com.daw.cinemadaw.domain.cinema.Screening;
import com.daw.cinemadaw.dto.SeatsListDTO;
import com.daw.cinemadaw.repository.CinemaRepository;
import com.daw.cinemadaw.repository.MovieRepository;
import com.daw.cinemadaw.repository.RoomRepository;
import com.daw.cinemadaw.repository.ScreeningRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ScreeningController {

    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;

    public ScreeningController(ScreeningRepository screeningRepository, RoomRepository roomRepository,
                               CinemaRepository cinemaRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.cinemaRepository = cinemaRepository;
        this.movieRepository = movieRepository;
    }

    private void populateScreeningFormModel(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        model.addAttribute("minScreeningDateTime", LocalDate.now().atStartOfDay().toString());
    }

    private void validateScreeningDate(Screening screening, BindingResult result) {
        if (screening.getDateTime() != null && screening.getDateTime().toLocalDate().isBefore(LocalDate.now())) {
            result.rejectValue("dateTime", "screening.dateTime.pastDay",
                    "La data de la projecció no pot ser anterior al dia actual");
        }
    }

    @GetMapping("/movies/projections/{id}")
    public String projections(@PathVariable Long id, Model model) {
        List<Screening> screenings = screeningRepository.findByMovieId(id);
        Optional<Movie> movie = movieRepository.findById(id);
        model.addAttribute("screenings", screenings);
        model.addAttribute("movieId", id);
        movie.ifPresent(m -> model.addAttribute("movie", m));
        return "projections/Screening";
    }

    @GetMapping("/screenings/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isPresent()) {
            model.addAttribute("screening", optional.get());
            return "projections/ScreeningDetail";
        }
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/movies/movies";
        }
        model.addAttribute("screening", optional.get());
        populateScreeningFormModel(model);
        model.addAttribute("cinemas", cinemaRepository.findAll());
        return "projections/ScreeningEdit";
    }

    @PostMapping("/screenings/update")
    public String update(@Valid @ModelAttribute("screening") Screening screening,
                         BindingResult result, Model model) {
        Long roomId = screening.getRoom() != null ? screening.getRoom().getId() : null;
        if (roomId == null || roomId <= 0) {
            result.rejectValue("room", "screening.room.required", "La sala és obligatòria");
        }
        validateScreeningDate(screening, result);

        if (result.hasErrors()) {
            populateScreeningFormModel(model);
            model.addAttribute("cinemas", cinemaRepository.findAll());
            return "projections/ScreeningEdit";
        }

        Optional<Screening> existingOpt = screeningRepository.findById(screening.getId());
        if (existingOpt.isEmpty()) {
            return "redirect:/movies/movies";
        }

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            result.rejectValue("room", "screening.room.invalid", "La sala seleccionada no és vàlida");
            populateScreeningFormModel(model);
            model.addAttribute("cinemas", cinemaRepository.findAll());
            return "projections/ScreeningEdit";
        }

        Screening existing = existingOpt.get();
        existing.setDateTime(screening.getDateTime());
        existing.setPrice(screening.getPrice());
        existing.setRoom(roomOpt.get());

        screeningRepository.save(existing);

        Long movieId = existing.getMovie() != null ? existing.getMovie().getId() : null;
        if (movieId != null) {
            return "redirect:/movies/projections/" + movieId;
        }
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/new")
    public String mostrarFormNew(@RequestParam Long movieId, Model model) {
        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) return "redirect:/movies/movies";
        Screening screening = new Screening();
        screening.setMovie(movie);
        model.addAttribute("screening", screening);
        populateScreeningFormModel(model);
        return "projections/ScreeningNew";
    }

    @PostMapping("/screenings/new")
    public String create(@Valid @ModelAttribute("screening") Screening screening,
                         BindingResult result, Model model) {
        Long movieId = screening.getMovie() != null ? screening.getMovie().getId() : null;
        Long roomId = screening.getRoom() != null ? screening.getRoom().getId() : null;
        if (movieId == null || movieId <= 0) {
            return "redirect:/movies/movies";
        }

        if (roomId == null || roomId <= 0) {
            result.rejectValue("room", "screening.room.required", "La sala és obligatòria");
        }
        validateScreeningDate(screening, result);

        if (result.hasErrors()) {
            populateScreeningFormModel(model);
            return "projections/ScreeningNew";
        }

        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) return "redirect:/movies/movies";

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            result.rejectValue("room", "screening.room.invalid", "La sala seleccionada no és vàlida");
            populateScreeningFormModel(model);
            return "projections/ScreeningNew";
        }

        screening.setRoom(roomOpt.get());
        screening.setMovie(movie);

        screeningRepository.save(screening);
        return "redirect:/movies/projections/" + movieId;
    }

    @PostMapping("/screenings/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Screening> optional = screeningRepository.findById(id);
        if (optional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La projecció no s'ha trobat.");
            return "redirect:/movies/movies";
        }
        Long movieId = optional.get().getMovie() != null ? optional.get().getMovie().getId() : null;
        try {
            screeningRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Projecció eliminada correctament.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No s'ha pogut eliminar la projecció. Pot tenir reserves associades.");
        }
        if (movieId != null) {
            return "redirect:/movies/projections/" + movieId;
        }
        return "redirect:/movies/movies";
    }

    @GetMapping("/screenings/reserve/{id}")
    public String reserve(@PathVariable Long id, Model model, HttpSession session, HttpServletRequest request) {
        Screening screening = screeningRepository.findById(id).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "redirect:/movies/movies";
        }
        boolean canReserve = !request.isUserInRole("ADMIN");

        java.util.List<com.daw.cinemadaw.domain.cinema.Seat> seats = screening.getRoom().getSeats();
        int minX = seats.stream().mapToInt(com.daw.cinemadaw.domain.cinema.Seat::getX).min().orElse(0);
        int maxX = seats.stream().mapToInt(com.daw.cinemadaw.domain.cinema.Seat::getX).max().orElse(9);
        int minY = seats.stream().mapToInt(com.daw.cinemadaw.domain.cinema.Seat::getY).min().orElse(0);
        int maxY = seats.stream().mapToInt(com.daw.cinemadaw.domain.cinema.Seat::getY).max().orElse(9);
        int seatPad = 20;
        int svgW = (maxX - minX) * 10 + 26 + 2 * seatPad;
        int svgH = (maxY - minY) * 10 + 74;

        SeatsListDTO seatsListDTO = new SeatsListDTO();
        seatsListDTO.setScreeningId(screening.getId());

        List<Long> preselectedIds = new ArrayList<>();
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof SeatsListDTO existingCart) {
            if (Objects.equals(existingCart.getScreeningId(), screening.getId())) {
                preselectedIds = new ArrayList<>(existingCart.getSeatIds());
                seatsListDTO.setSeatIds(preselectedIds);
            }
        } else if (cartObj instanceof List<?> rawItems) {
            for (Object item : rawItems) {
                if (item instanceof SeatsListDTO existingCart && Objects.equals(existingCart.getScreeningId(), screening.getId())) {
                    preselectedIds = new ArrayList<>(existingCart.getSeatIds());
                    seatsListDTO.setSeatIds(preselectedIds);
                    break;
                }
            }
        }

        model.addAttribute("seatsListDTO", seatsListDTO);
        model.addAttribute("seats", seats);
        model.addAttribute("screening", screening);
        model.addAttribute("svgW", svgW);
        model.addAttribute("svgH", svgH);
        model.addAttribute("svgMidX", svgW / 2);
        model.addAttribute("svgRectW", svgW - 2 * seatPad);
        model.addAttribute("seatPad", seatPad);
        model.addAttribute("seatMinX", minX);
        model.addAttribute("seatMinY", minY);
        model.addAttribute("preselectedIds", preselectedIds);
        model.addAttribute("canReserve", canReserve);

        return "projections/ScreeningReserve";
    }

    @PostMapping("/screenings/reserve")
    public String reserveSeats(@ModelAttribute("seatsListDTO") SeatsListDTO selectedSeats, HttpSession session,
                               RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Long screeningId = selectedSeats.getScreeningId();

        if (request.isUserInRole("ADMIN")) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Com a administrador només pots visualitzar el mapa de seients.");
            if (screeningId != null) {
                return "redirect:/screenings/reserve/" + screeningId;
            }
            return "redirect:/movies/movies";
        }

        if (screeningId == null) {
            return "redirect:/movies/movies";
        }

        List<Long> selectedSeatIds = selectedSeats.getSeatIds();
        if (selectedSeatIds == null || selectedSeatIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Selecciona almenys un seient abans de continuar.");
            return "redirect:/screenings/reserve/" + screeningId;
        }

        Screening screening = screeningRepository.findById(screeningId).orElse(null);
        if (screening == null || screening.getRoom() == null) {
            return "redirect:/movies/movies";
        }

        List<SeatsListDTO> cartItems = new ArrayList<>();
        Object cartObj = session.getAttribute("cart");
        if (cartObj instanceof SeatsListDTO existingCart) {
            cartItems.add(existingCart);
        } else if (cartObj instanceof List<?> rawItems) {
            for (Object item : rawItems) {
                if (item instanceof SeatsListDTO existingCart) {
                    cartItems.add(existingCart);
                }
            }
        }

        boolean updated = false;
        for (SeatsListDTO cartItem : cartItems) {
            if (Objects.equals(cartItem.getScreeningId(), screeningId)) {
                cartItem.setSeatIds(new ArrayList<>(selectedSeatIds));
                updated = true;
                break;
            }
        }

        if (!updated) {
            SeatsListDTO cart = new SeatsListDTO();
            cart.setScreeningId(screeningId);
            cart.setSeatIds(new ArrayList<>(selectedSeatIds));
            cartItems.add(cart);
        }

        session.setAttribute("cart", cartItems);

        return "redirect:/cart";
    }
}
