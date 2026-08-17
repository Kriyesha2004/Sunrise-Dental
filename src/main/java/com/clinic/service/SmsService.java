package com.clinic.service;

public interface SmsService {
    void sendSms(String contactNumber, String message);
}
