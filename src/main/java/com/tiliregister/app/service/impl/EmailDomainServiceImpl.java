package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.EmailDomainDao;
import com.tiliregister.app.model.EmailDomain;
import com.tiliregister.app.model.User;
import com.tiliregister.app.service.EmailDomainService;
import com.tiliregister.app.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailDomainServiceImpl implements EmailDomainService {

    private final EmailDomainDao emailDomainDao;
    private final UserService userService;

    @Autowired
    public EmailDomainServiceImpl(EmailDomainDao emailDomainDao, @Lazy UserService userService) {
        this.emailDomainDao = emailDomainDao;
        this.userService = userService;
    }

    @Override
    public EmailDomain saveEmailDomain(EmailDomain emailDomain, String createdByUsername) {
        User createdBy = userService.getUserByUsername(createdByUsername);

        if(createdBy == null || createdBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        emailDomain.setCreatedBy(createdBy);
        emailDomain.setCreatedAt(LocalDateTime.now());

        return emailDomainDao.save(emailDomain);
    }

    @Override
    public EmailDomain updateEmailDomain(Long id, EmailDomain emailDomain, String updatedByUsername) {
        User updatedBy = userService.getUserByUsername(updatedByUsername);

        if(updatedBy == null || updatedBy.getVoided() == 1){
            throw new EntityNotFoundException("Admin not found");
        }
        EmailDomain updatedEmailDomain = emailDomainDao.findById(id);

        updatedEmailDomain.setHost(emailDomain.getHost());
        updatedEmailDomain.setUsername(emailDomain.getUsername());
        updatedEmailDomain.setPassword(emailDomain.getPassword());
        updatedEmailDomain.setPort(emailDomain.getPort() == null ? emailDomain.getPort() : "587");
        updatedEmailDomain.setCreatedBy(updatedBy);
        updatedEmailDomain.setCreatedAt(LocalDateTime.now());

        return emailDomainDao.save(updatedEmailDomain);
    }

    @Override
    public EmailDomain getEmailDomainById(Long id) {
        return emailDomainDao.findById(id);
    }

    @Override
    public List<EmailDomain> getAllEmailDomains() {
        return emailDomainDao.findAll();
    }
}
