package com.project.hexagonal.offer.api.controller;

import com.project.hexagonal.offer.api.dto.CreateOfferRequest;
import com.project.hexagonal.offer.api.mapper.OfferApiMapper;
import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferRestController {

    private final OfferApiMapper mapper;
    private final OfferApplicationService applicationService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TEST_ROLE')")
    public ResponseEntity<String> create(@Valid @RequestBody CreateOfferRequest request) {
        applicationService.create(mapper.toCreateCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body("Offer created successfully...");
    }

    @PostMapping("/cancel/{offerId}")
    @PreAuthorize("hasAnyRole('TEST_ROLE')")
    public ResponseEntity<String> cancel(@PathVariable("offerId") UUID offerId) {
        applicationService.cancel(offerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Offer cancelled successfully...");
    }
}
