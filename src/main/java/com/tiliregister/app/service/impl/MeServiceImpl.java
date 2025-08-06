package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.MeDao;
import com.tiliregister.app.util.InternetConnectionChecker;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.EmailService;
import com.tiliregister.app.service.MeService;
import com.tiliregister.app.service.UserAuthenticationService;
import com.tiliregister.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MeServiceImpl implements MeService {

    private final MeDao meDao;
    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;
    private final EmailService emailService;

    @Autowired
    public MeServiceImpl(MeDao meDao, UserService userService, UserAuthenticationService userAuthenticationService, EmailService emailService) {
        this.meDao = meDao;
        this.userService = userService;
        this.userAuthenticationService = userAuthenticationService;
        this.emailService = emailService;
    }

    @Override
    public User getMe(String username) {
        Long meId = userService.getUserByUsername(username).getId();
        return meDao.findMeById(meId);
    }

    @Override
    public User updateMe(String username, User user, String updatedByUsername) {
        User existingMe = userService.getUserByUsername(username);

        if (userService.isEmailUnique(user.getEmailAddress(), existingMe.getId())) {
            throw new IllegalArgumentException("Email address already exists");
        }
        boolean sendNotification = !Objects.equals(existingMe.getUsername(), user.getUsername());

        existingMe.setUsername(user.getEmailAddress().toLowerCase().trim());
        existingMe.setSurname(user.getSurname());
        existingMe.setOthernames(user.getOthernames());
        existingMe.setEmailAddress(user.getEmailAddress().toLowerCase().trim());
        existingMe.setContactNumber(user.getContactNumber());

        if(sendNotification) {
            //Send an email
            if (InternetConnectionChecker.isInternetAvailable()) {
                emailService.sendEmail(
                        existingMe.getEmailAddress(),
                        "Tili - Email Change!",
                        "Dear " + existingMe.getOthernames() + " " + existingMe.getSurname() + ",\n\n" +
                                "This is to let you know that your email address has changed.\n\n" +
                                "Below are your credentials:\n\n" +
                                "New username: " + existingMe.getUsername() + "\n\n" +
                                "If you did not initiate this change, notify administrator as soon as possible.\n\n" +
                                "Regards,\n" +
                                "System Admin"
                );
            }
        }
        return meDao.updateMe(existingMe);
    }

    @Override
    public boolean changeMePassword(String username, String currentPassword, String newPassword) {
        boolean isAuthenticated = userAuthenticationService.authenticateUser(
                username,
                currentPassword);

        if (isAuthenticated) {
            return userAuthenticationService.changePasswordProcess(username, newPassword, "UPDATED");
        } else {
            throw new IllegalArgumentException("The current password you entered is incorrect. Please try again.");
         }
    }
}