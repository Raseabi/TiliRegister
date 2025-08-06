package com.tiliregister.app.service.impl;

import com.tiliregister.app.dao.ClientDao;
import com.tiliregister.app.model.Client;
import com.tiliregister.app.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

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
