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
        Offer domainEntity = appMapper.toDomain(command);
        domainEntity.validateAndInitialize();
        persistencePort.save(domainEntity);
    }

    @Override
    @Transactional
    public void publish(UUID offerId) {
        Offer domainEntity = persistencePort.findById(offerId);
        domainEntity.processStatus();
        persistencePort.save(domainEntity);
        publishEvent(domainEntity);
    }

    @Override
    @Transactional
    public void cancel(UUID offerId) {
        Offer domainEntity = persistencePort.findById(offerId);
        domainEntity.close();
        persistencePort.save(domainEntity);
        publishEvent(domainEntity);
    }

    private void publishEvent(Offer domainEntity) {
        domainEntity.getDomainEvents()
                .forEach(eventPublisher::publishEvent);
        domainEntity.clearDomainEvents();
    }
}
