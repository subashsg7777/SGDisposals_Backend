package com.subash.SGDisposals.service;

import com.subash.SGDisposals.exception.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public boolean sendOtp(String toEmail, String otp){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("subashsg7777@gmail.com");
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("Your OTP To Validate Email In SG_Disposals Service");
            mailMessage.setText("Here is the verification Code to Confirm Your Email :" + otp + "This will invalid After 10 mins So Please Be Carefull");
            javaMailSender.send(mailMessage);
            return true;
        }

        catch (Exception ee){
            throw new InvalidRequestStateException("Can't Send verification Email Right Now!");
        }
    }

    public boolean sendRequestAck(String email,String address){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setFrom("subashsg7777@gmail.com");
            simpleMailMessage.setSubject("We Got You're Request For Garbage Collection");
            simpleMailMessage.setText("We Received An Collection Request From "+email+ " \n Address : \n"+address+ "\nOur Collector will contact You As Soon As Possible ");
            javaMailSender.send(simpleMailMessage);
            return true;
        }
        catch (Exception ee){
            throw new InvalidRequestStateException("Cannot Send Ack Mail For new Request");
        }
    }

    public boolean sendCollectionRecipt(String email, Map<String, Long> weights, int total){

        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setFrom("subashsg7777@gmail.com");
            simpleMailMessage.setSubject("Here Is You Receipt for Garbage Collection");
            simpleMailMessage.setText("Below is Your Points Receipt From the Request " +
                    "\n"+ weights + " \n *All The Weights Mentioned Here is on Grams * " +
                    "\n Your Total Points Income From This request is : "+ total);
            javaMailSender.send(simpleMailMessage);
            return true;
        }

        catch (Exception ee){
            throw new InvalidRequestStateException("Can't Send Points Receipt Right now");
        }
    }

    public boolean sendOrderReceipt(String email, String order_id, Integer product_id, String product_name, Float points,
                                     Integer quantity, Float total_points){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("subashsg7777@gmail.com");
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Here is Your Order Receipt for Order :"+order_id);
            simpleMailMessage.setText("id \t productName \t points \t Quantity \t total_points\n"
                    +product_id+" \t "+product_name+" \t "+points+" \t "+quantity+" \t "+total_points);
            javaMailSender.send(simpleMailMessage);
            return true;
        }
        catch (Exception e) {
            throw new InvalidRequestStateException("Can't Send OrderReceipt Right now");
        }
    }
}
