package com.tiliregister.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clients", uniqueConstraints = @UniqueConstraint(columnNames = "clientNumber"))
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_number", nullable = false, unique = true)
    String clientNumber;

    @Column(name = "client_name", nullable = false)
    String clientName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", clientNumber='" + clientNumber + '\'' +
                ", clientName='" + clientName + '\'' +
                '}';
    }
}
