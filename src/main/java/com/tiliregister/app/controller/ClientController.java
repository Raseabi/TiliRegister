package com.tiliregister.app.controller;

import com.tiliregister.app.model.Client;
import com.tiliregister.app.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PreAuthorize("hasAuthority('client:create')")
    @PostMapping
    ResponseEntity<Client> createClient(@RequestBody Client client, Authentication authentication){
        String username = authentication.getName();
        Client savedClient = clientService.saveClient(client);
        return ResponseEntity.ok(savedClient);
    }
    @PreAuthorize("hasAuthority('client:view')")
    @GetMapping
    ResponseEntity<Page<Client>> searchClients(
            @RequestParam(name = "searchTerm", required = false) String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "clientNumber") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ){
        Page<Client> result = clientService.searchClients(
                searchTerm, page, size, sortField, sortOrder
        );
        return ResponseEntity.ok(result);
    }
}
