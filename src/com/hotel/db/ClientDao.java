package com.hotel.db;

import com.hotel.model.Client;

import java.util.List;

public interface ClientDao {
    Client add(Client client);
    List<Client> findAll();
    Client findByName(String name);
    Client findById(int id);

}
