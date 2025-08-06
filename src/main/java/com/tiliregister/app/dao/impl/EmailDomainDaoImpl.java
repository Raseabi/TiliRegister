package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.EmailDomainDao;
import com.tiliregister.app.model.EmailDomain;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class EmailDomainDaoImpl implements EmailDomainDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EmailDomain save(EmailDomain emailDomain) {
        if(emailDomain.getId() == null){
            entityManager.persist(emailDomain);
            return emailDomain;
        } else {
            return entityManager.merge(emailDomain);
        }
    }

    @Override
    public List<EmailDomain> findAll() {
        return entityManager.createQuery("from EmailDomain", EmailDomain.class)
                .getResultList();
    }

    @Override
    public EmailDomain findById(Long id) {
        return entityManager.find(EmailDomain.class, id);
    }
}
