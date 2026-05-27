package com.project.hexagonal.bid.api.controller;

import com.project.hexagonal.bid.api.dto.BidApplyRequest;
import com.project.hexagonal.bid.api.mapper.BidApiMapper;
import com.project.hexagonal.bid.application.contract.input.BidApplicationService;
import com.project.hexagonal.bid.application.dto.command.BidApplyCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidRestController {

    private final BidApplicationService bidApplicationService;
    private final BidApiMapper mapper;

    @PostMapping("/apply/{offerId}")
    public ResponseEntity<String> applyBid(@PathVariable("offerId") UUID offerId,
                                           @Valid @RequestBody BidApplyRequest request) {
        BidApplyCommand command = mapper.toCommand(request, offerId);
        bidApplicationService.applyBid(command);
        return ResponseEntity.status(HttpStatus.CREATED).body("Bid applied successfully...");
    }

}
