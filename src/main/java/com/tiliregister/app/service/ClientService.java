package com.tiliregister.app.service;

import com.tiliregister.app.model.Client;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClientService {
    Client saveClient(Client client);
    Client getOrCreateClient(String clientNumber, String clientName);
    Client getClientById(Long id);
    Client getClientByNumber(String clientNumber);
    List<Client> getClientByName(String clientName);
    Page<Client> searchClients(String searchToken, int page, int size, String sortField, String sortOrder);
}
