package it.polito.ai.virtuallabs.services;

public interface NotificationService {
    void sendConfirmMessage(String address, String role, String serialNumber);

}
