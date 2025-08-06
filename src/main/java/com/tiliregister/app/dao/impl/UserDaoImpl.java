package com.tiliregister.app.dao.impl;

import com.tiliregister.app.dao.UserDao;
import com.tiliregister.app.model.User;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    @Override
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User findByUsername(String username) {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u " +
                                "LEFT JOIN FETCH u.userRoles ur " +
                                "LEFT JOIN FETCH ur.role r " +
                                "LEFT JOIN FETCH r.rolePermissions rp " +
                                "LEFT JOIN FETCH rp.permission p " +
                                "WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
    }

    @Override
    public User findByEmailAddress(String emailAddress) {
        List<User> query = entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.emailAddress = :emailAddress", User.class)
                .setParameter("emailAddress", emailAddress)
                .setMaxResults(1)
                .getResultList();

        return query.isEmpty() ? null : query.get(0);
    }

    @Override
    public List<User> findByVoidStatus(List<Integer> voidStatus) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.voided IN :voided", User.class)
                .setParameter("voided", voidStatus)
                .getResultList();
    }

    @Override
    public boolean isEmailUnique(String emailAddress, Long excludeUserId) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.emailAddress = :emailAddress";

        if (excludeUserId != null) {
            jpql += " AND u.id <> :excludeUserId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("emailAddress", emailAddress);

        if (excludeUserId != null) {
            query.setParameter("excludeUserId", excludeUserId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean isUsernameUnique(String username, Long excludeUserId) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username";

        if (excludeUserId != null) {
            jpql += " AND u.id <> :excludeUserId";
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("username", username);

        if (excludeUserId != null) {
            query.setParameter("excludeUserId", excludeUserId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Page<User> searchUsers(String searchToken, int page, int size, String sortField, String sortOrder) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> user = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";

            predicates.add(cb.or(
                    cb.like(cb.lower(user.get("surname")), likeToken),
                    cb.like(cb.lower(user.get("othernames")), likeToken),
                    cb.like(cb.lower(user.get("emailAddress")), likeToken),
                    cb.like(cb.lower(user.get("contactNumber")), likeToken),
                    cb.like(cb.lower(user.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(user.get("createdBy").get("othernames")), likeToken)
            ));
        } else {
            predicates.add(cb.equal(user.get("voided"), 0));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        if (sortField != null && !sortField.isBlank()) {
            Path<Object> sortPath = user.get(sortField);
            cq.orderBy("desc".equalsIgnoreCase(sortOrder) ? cb.desc(sortPath) : cb.asc(sortPath));
        }

        // Fetch paginated result
        TypedQuery<User> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<User> users = query.getResultList();

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot));

        // Rebuild the same predicates with countRoot
        List<Predicate> countPredicates = new ArrayList<>();
        if (searchToken != null && !searchToken.trim().isEmpty()) {
            String likeToken = "%" + searchToken.trim().toLowerCase() + "%";
            countPredicates.add(cb.or(
                    cb.like(cb.lower(countRoot.get("surname")), likeToken),
                    cb.like(cb.lower(countRoot.get("othernames")), likeToken),
                    cb.like(cb.lower(countRoot.get("emailAddress")), likeToken),
                    cb.like(cb.lower(countRoot.get("contactNumber")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("surname")), likeToken),
                    cb.like(cb.lower(countRoot.get("createdBy").get("othernames")), likeToken)
            ));
        }  else {
            countPredicates.add(cb.equal(countRoot.get("voided"), 0));
        }
        countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, PageRequest.of(page, size), totalCount);
    }
}