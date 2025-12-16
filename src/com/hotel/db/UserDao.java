package com.hotel.db;

import com.hotel.model.User;
import java.util.List;

public interface UserDao {

    User findById(int id);

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    List<User> findAll();

    User save(User user);       // insert

    void update(User user);     // update (password, role, client_id)

    void delete(int id);
}
