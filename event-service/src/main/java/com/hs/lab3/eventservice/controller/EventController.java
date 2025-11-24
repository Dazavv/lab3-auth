package com.hs.lab3.eventservice.controller;

import com.hs.lab3.eventservice.auth.jwt.JwtAuthentication;
import com.hs.lab3.eventservice.auth.jwt.JwtProvider;
import com.hs.lab3.eventservice.dto.requests.CreateEventRequest;
import com.hs.lab3.eventservice.dto.requests.CreateUserEventRequest;
import com.hs.lab3.eventservice.dto.responses.EventDto;
import com.hs.lab3.eventservice.entity.Event;
import com.hs.lab3.eventservice.mapper.EventMapper;
import com.hs.lab3.eventservice.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final JwtProvider jwtProvider;


    @PostMapping
    @PreAuthorize("hasAnyAuthority('REDACTOR')")
    public Mono<ResponseEntity<EventDto>> addEvent(@Valid @RequestBody CreateEventRequest request) {
        Mono<Event> event = eventService.addEvent(
                request.name(),
                request.description(),
                request.date(),
                request.startTime(),
                request.endTime(),
                request.ownerId());
        return event.map(eventMapper::toEventDto)
                .map(ResponseEntity::ok);
    }
    @PostMapping("/my")
    @PreAuthorize("hasAnyAuthority('USER', 'LEAD')")
    public Mono<ResponseEntity<EventDto>> addMyEvent(
            @Valid @RequestBody CreateUserEventRequest request,
            Authentication authentication
    ) {
        JwtAuthentication auth = (JwtAuthentication) authentication;
        log.info("OwnerId from auth: {}", auth.getId());

        return eventService.addEvent(
                        request.name(),
                        request.description(),
                        request.date(),
                        request.startTime(),
                        request.endTime(),
                        auth.getId()
                )
                .map(eventMapper::toEventDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('USER', 'LEAD')")
    public Flux<EventDto> getMyEvents(Authentication authentication) {
        JwtAuthentication auth = (JwtAuthentication) authentication;
        return eventService.getEventsByOwnerId(auth.getId())
                .map(eventMapper::toEventDto);
    }

    @GetMapping("/my/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'LEAD')")
    public Mono<EventDto> getMyEventById(Authentication authentication, @PathVariable @Min(1) Long id) {
        JwtAuthentication auth = (JwtAuthentication) authentication;
        return eventService.getEventByOwnerId(auth.getId(), id)
                .map(eventMapper::toEventDto);
    }
    @DeleteMapping(path = "/my/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'LEAD')")
    public Mono<ResponseEntity<Void>> deleteMyEventById(Authentication authentication, @PathVariable @Min(1) Long id) {
        JwtAuthentication auth = (JwtAuthentication) authentication;
        return eventService.deleteEventByOwnerId(auth.getId(), id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('REDACTOR')")
    public Flux<EventDto> getAllEvents() {
        return eventService.getAllEvents()
                .map(eventMapper::toEventDto);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority('REDACTOR')")
    public Mono<ResponseEntity<EventDto>> getEventById(@PathVariable @Min(1) Long id) {
        return eventService.getEventById(id)
                .map(eventMapper::toEventDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/owner/{id}")
    @PreAuthorize("hasAnyAuthority('REDACTOR')")
    public Flux<Event> getUserEvents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return eventService.getUserEventsById(id, pageable);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority('REDACTOR')")
    public Mono<ResponseEntity<Void>> deleteEventById(@PathVariable @Min(1) Long id) {
        return eventService.deleteEventById(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping("/busy")
    @PreAuthorize("hasAnyAuthority('SERVICE')")
    public Flux<EventDto> getBusyEventsForUsersBetweenDates(
            @RequestParam List<Long> userIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return eventService.getBusyEventsForUsersBetweenDates(userIds, startDate, endDate)
                .map(eventMapper::toEventDto);
    }
}
