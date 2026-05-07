package com.example.userservice.service;

public interface NotificationService {
    void sendStatusChangeNotification(String userEmail, String newState, String reason);
}