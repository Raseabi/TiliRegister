package com.tiliregister.app.service;

import com.tiliregister.app.model.EmailDomain;

import java.util.List;

public interface EmailDomainService {
    EmailDomain saveEmailDomain(EmailDomain emailDomain, String createdByUsername);
    EmailDomain updateEmailDomain(Long id, EmailDomain emailDomain, String updatedByUsername);
    EmailDomain getEmailDomainById(Long id);
    List<EmailDomain> getAllEmailDomains();
}
