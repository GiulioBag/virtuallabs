package it.polito.ai.virtuallabs.services;

import it.polito.ai.virtuallabs.entities.ConfirmToken;
import it.polito.ai.virtuallabs.repositories.ConfirmTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private ConfirmTokenRepository confirmTokenRepository;

    @Override
    public void sendConfirmMessage(String address, String role, String serialNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        // TODO per debbug ho messo la mia mail
        //message.setTo(address);
        message.setTo("lorenzo_vaiani@yahoo.com");

        // TODO è possibile verificare la mail fino a due giorni dopo
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime() + TimeUnit.HOURS.toMillis(48));
        ConfirmToken confirmToken = new ConfirmToken(UUID.randomUUID().toString(), serialNumber, timestamp);

        String body = "Dear " + role + " \nyour account it is been correctly register to PoliTo site. To activate" +
                " your account you have to click to the following link: http://192.168.10.10:8080/API/user/confirm/" + confirmToken.getId();

        String subject = "Confirm Registration";
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);

        confirmTokenRepository.save(confirmToken);
    }


}
