package com.subash.SGDisposals.util;

import com.subash.SGDisposals.exception.UnauthorizedRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OtpManager {

    private static final Logger log = LoggerFactory.getLogger(OtpManager.class);
    private final ConcurrentHashMap<String, otpStorage> concurrentHashMap = new ConcurrentHashMap<>();

    public int generateRandomOtp(String email) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        concurrentHashMap.put(email, new otpStorage(String.valueOf(otp), Instant.now().plusSeconds(300)));
        return otp;
    }

    public static class otpStorage{

        String otp;
        Instant expiry;

        otpStorage(String otp, Instant expiry){
            this.otp = otp;
            this.expiry = expiry;        }
    }

    public boolean verifyOtp(String email, String otp){
        otpStorage storedOtp = concurrentHashMap.get(email);
        if(storedOtp == null){
            return false;
        }
        if(Instant.now().isAfter(storedOtp.expiry)){
            log.info("OTP is Expired");
            return false;
        }
        boolean result = otp.equals(storedOtp.otp);
        if(!result){
            log.info("OTP Does Not Match");
        }
        return result;
    }

    public boolean verifyForgot(String email, String otp){
        otpStorage storage = concurrentHashMap.get(email);
        if(storage == null){
            throw new UnauthorizedRequestException("Invalid Otp");
        }

        if(Instant.now().isAfter(storage.expiry)){
            throw new UnauthorizedRequestException("Expired Otp");
        }

        boolean result = otp.equals(storage.otp);
        if(!result){
            throw new UnauthorizedRequestException("Incorrect OTP");
        }
        return  result;
    }
}