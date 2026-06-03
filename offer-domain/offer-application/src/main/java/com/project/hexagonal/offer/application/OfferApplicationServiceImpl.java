package com.project.hexagonal.offer.application;

import com.project.hexagonal.offer.application.contract.input.OfferApplicationService;
import com.project.hexagonal.offer.application.contract.output.persistence.OfferPersistencePort;
import com.project.hexagonal.offer.application.dto.command.CreateOfferCommand;
import com.project.hexagonal.offer.application.mapper.OfferAppMapper;
import com.project.hexagonal.offer.core.model.Offer;
import com.project.hexagonal.shared.application.annotation.DomainService;
import com.project.hexagonal.shared.application.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@DomainService
@RequiredArgsConstructor
public class OfferApplicationServiceImpl implements OfferApplicationService {

    private final OfferAppMapper appMapper;
    private final OfferPersistencePort persistencePort;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public void create(CreateOfferCommand command) {
        Offer offer = appMapper.toDomain(command);
        offer.validateAndInitialize();
        persistencePort.save(offer);
    }

    @Override
    @Transactional
    public void publish(UUID offerId) {
        Offer offer = persistencePort.findById(offerId);
        offer.processStatus();
        persistencePort.save(offer);
        publishEvents(offer);
    }

    @Override
    @Transactional
    public void cancel(UUID offerId) {
        Offer offer = persistencePort.findById(offerId);
        offer.cancel();
        persistencePort.save(offer);
        publishEvents(offer);
    }

    @Override
    @Transactional
    public void updateStatusOnBidAccepted(UUID offerId) {
        Offer offer = persistencePort.findById(offerId);
        offer.updateStatus();
        persistencePort.save(offer);
        publishEvents(offer);
    }

    private void publishEvents(Offer offer) {
        offer.getDomainEvents().forEach(eventPublisher::publishEvent);
        offer.clearDomainEvents();
    }
}
