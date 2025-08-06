package com.tiliregister.app.controller;


import com.tiliregister.app.model.EmailDomain;
import com.tiliregister.app.service.EmailDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/email-domain")
public class EmailDomainContoller {

    private final EmailDomainService emailDomainService;

    @Autowired
    public EmailDomainContoller(EmailDomainService emailDomainService) {
        this.emailDomainService = emailDomainService;
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping
    ResponseEntity<List<EmailDomain>> getAllEmailDomains(){
        return ResponseEntity.ok(emailDomainService.getAllEmailDomains());
    }

    @PreAuthorize("hasAuthority('setting:view')")
    @GetMapping("/{id}")
    ResponseEntity<EmailDomain> getEmailDomainById(@PathVariable Long id){
        return ResponseEntity.ok(emailDomainService.getEmailDomainById(id));
    }

    @PreAuthorize("hasAuthority('setting:edit')")
    @PutMapping("/{id}")
    ResponseEntity<EmailDomain> updateEmailDomain(@PathVariable Long id, @RequestBody EmailDomain emailDomain, Authentication authentication){
        String username  = authentication.getName();
        return ResponseEntity.ok(emailDomainService.updateEmailDomain(id, emailDomain, username));
    }
}
