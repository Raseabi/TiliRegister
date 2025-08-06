package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.ClientDao;
import com.tiliregister.app.model.Client;
import com.tiliregister.app.model.Client;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ClientDaoImpl implements ClientDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Client save(Client client) {
        if(client.getId() == null){
            entityManager.persist(client);
            return client;
        } else {
            return entityManager.merge(client);
        }
    }

    @Override
    public Client findById(Long id) {
        return entityManager.find(Client.class, id);
    }

    @Override
    public Client findByNumber(String clientNumber) {
        List<Client> query = entityManager.createQuery("SELECT c FROM Client c WHERE c.clientNumber = :clientNumber", Client.class)
                .setParameter("clientNumber", clientNumber)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<Client> findByName(String clientName) {
        return entityManager.createQuery("SELECT c FROM Client c WHERE c.clientName = :clientName", Client.class)
                .setParameter("clientName", clientName)
                .getResultList();
    }

    @Override
    public Page<Client> searchClients(String searchToken, int page, int size, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(client.get("clientName")), likeToken),
                    cb.like(cb.lower(client.get("clientNumber")), likeToken)
                ));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        if (sortField != null && !sortField.isBlank()) {
            Path<Object> sortPath = client.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(sortPath) : cb.asc(sortPath));
        }

        // Fetch paginated result
        TypedQuery<Client> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Client> clients = query.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Client> countRoot = countQuery.from(Client.class);
        countQuery.select(cb.count(countRoot));

        // Rebuild the same predicates with countRoot
        List<Predicate> countPredicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            countPredicates.add(cb.or(
                    cb.like(cb.lower(countRoot.get("clientNumber")), likeToken),
                    cb.like(cb.lower(countRoot.get("clientName")), likeToken)
              ));
        }
        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(clients, PageRequest.of(page, size), totalCount);

    }
}
