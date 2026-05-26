package com.project.hexagonal.offer.api.controller;

import com.project.hexagonal.offer.api.dto.CreateOfferRequest;
import com.project.hexagonal.offer.api.mapper.OfferApiMapper;
import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferApiMapper mapper;
    private final OfferApplicationService applicationService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TEST_ROLE')")
    public ResponseEntity<String> create(@RequestBody CreateOfferRequest request) {
        applicationService.create(mapper.toCreateCommand(request));
        return ResponseEntity.ok("Offer created successfully...");
    }
}
