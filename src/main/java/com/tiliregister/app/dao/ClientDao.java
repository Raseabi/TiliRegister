package com.tiliregister.app.dao;

import com.tiliregister.app.model.Client;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientDao {
    Client save(Client client);
    Client findById(Long id);
    Client findByNumber(String clientNumber);
    List<Client> findByName(String clientName);
    Page<Client> searchClients(String searchToken, int page, int size, String sortField, String sortOrder);
}
