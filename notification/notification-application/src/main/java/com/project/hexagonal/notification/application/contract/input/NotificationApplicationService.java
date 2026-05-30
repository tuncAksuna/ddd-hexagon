package com.project.hexagonal.notification.application.contract.input;

public interface NotificationApplicationService {

    void notify(String message, Object... args);
}
