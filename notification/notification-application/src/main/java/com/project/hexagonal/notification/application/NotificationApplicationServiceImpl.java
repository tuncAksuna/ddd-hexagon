package com.project.hexagonal.notification.application;

import com.project.hexagonal.notification.application.contract.input.NotificationApplicationService;
import com.project.hexagonal.shared.application.annotation.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@DomainService
@RequiredArgsConstructor
public class NotificationApplicationServiceImpl implements NotificationApplicationService {

    @Override
    public void notify(String message, Object... args) {
        if (Objects.nonNull(message) && !message.isBlank())
            log.info(String.format(message, args));
    }
}
