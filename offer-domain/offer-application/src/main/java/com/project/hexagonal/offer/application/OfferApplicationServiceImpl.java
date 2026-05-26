package com.project.hexagonal.offer.application;

import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;
import com.project.hexagonal.offer.application.mapper.OfferAppMapper;
import com.project.hexagonal.shared.application.annotation.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DomainService
@RequiredArgsConstructor
public class OfferApplicationServiceImpl implements OfferApplicationService {

    private final OfferAppMapper appMapper;

    @Override
    public void create(CreateOfferCommand command) {

    }
}
