package com.clinic.service.impl;

import com.clinic.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public void sendSms(String contactNumber, String message) {
        logger.info("[SMS GATEWAY SIMULATOR] Sending SMS to target contact: {}", contactNumber);
        logger.info("[SMS GATEWAY SIMULATOR] MESSAGE CONTENT:\n--------------------------------------------------\n{}\n--------------------------------------------------", message);
        System.out.println("\n[SMS GATEWAY SIMULATOR] Sent SMS to " + contactNumber + ": \"" + message + "\"\n");
    }
}
