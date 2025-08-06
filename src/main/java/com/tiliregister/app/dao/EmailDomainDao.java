package com.tiliregister.app.dao;

import com.tiliregister.app.model.EmailDomain;

import java.util.List;

public interface EmailDomainDao {
    EmailDomain save(EmailDomain emailDomain);
    List<EmailDomain> findAll();
    EmailDomain findById(Long id);
}
