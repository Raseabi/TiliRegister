package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.ClientDao;
import com.tiliregister.app.model.Client;
import com.tiliregister.app.service.ClientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientDao clientDao;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public Client saveClient(Client client) {
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public Client getOrCreateClient(String clientNumber, String clientName) {
        return Optional.ofNullable(clientDao.findByNumber(clientNumber))
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setClientNumber(clientNumber);
                    newClient.setClientName(clientName);
                    return clientDao.save(newClient);
                });
    }

    @Override
    public Client getClientById(Long id) {
        return clientDao.findById(id);
    }

    @Override
    public Client getClientByNumber(String clientNumber) {
        return clientDao.findByNumber(clientNumber);
    }

    @Override
    public List<Client> getClientByName(String clientName) {
        return clientDao.findByName(clientName);
    }

    @Override
    public Page<Client> searchClients(String searchToken, int page, int size, String sortField, String sortOrder) {
        return clientDao.searchClients(searchToken, page, size, sortField, sortOrder);
    }
}
