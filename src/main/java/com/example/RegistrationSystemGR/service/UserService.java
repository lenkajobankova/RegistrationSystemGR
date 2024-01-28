package com.example.RegistrationSystemGR.service;

import com.example.RegistrationSystemGR.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    private String sqlCreateUser = "INSERT into registrationsystem.users(" +
            "id, name, surname, personId, uuid) VALUES (?, ?, ?, ?, ?)";
    private String sqlGetUserById = "SELECT * from registrationsystem.users u WHERE u.id = ";
    private String sqlGetAllUsers = "SELECT * from registrationsystem.users";
    private String sqlUpdateUser = "UPDATE registrationsystem.users SET name = ?, surname = ? WHERE id =?";
    private String sqlDeteleUser = "DELETE from registrationsystem.users where id = ";
    private String filename = "ValidatedPersonId.txt";

    public User createNewUser(User user) throws FileNotFoundException {
        try {
            jdbcTemplate.update(sqlCreateUser,
                    user.getId(), user.getName(), user.getSurname(), validatedPersonId(filename), getGeneratedUuid());
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Error during reading file " + filename + " " + e.getLocalizedMessage());
        }
        return user;
    }

    public User getUserById(long id, String detail) {
        if (detail == null) {
            return getUserByIdShort(id);
        } else if (detail.equals("true")) {
            return getUserByIdDetail(id);
        }
        return null;
    }

    public List<User> getAllUsers(String detail) {
        if (detail == null) {
            return getAllUsersShort();
        } else if (detail.equals("true")) {
            return getAllUsersDetail();
        }
        return null;
    }

    public void updateUser(User user, long id) {
        jdbcTemplate.update(sqlUpdateUser, user.getName(), user.getSurname(), id);
    }

    public void deleteUser(int id) {
        jdbcTemplate.update(sqlDeteleUser + id);
    }


    private User getUserByIdShort(long id) {
        User user = (User) jdbcTemplate.queryForObject(sqlGetUserById + id, new RowMapper<Object>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = getUser(rs);
                return user;
            }
        });
        return user;
    }

    private User getUserByIdDetail(long id) {
        User user = (User) jdbcTemplate.queryForObject(sqlGetUserById + id, new RowMapper<Object>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = getUser(rs);
                user.setPersonId(rs.getString("personId"));
                user.setUuid(rs.getString("uuid"));
                return user;
            }
        });
        return user;
    }

    private List<User> getAllUsersShort() {
        List<User> users = jdbcTemplate.query(sqlGetAllUsers, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = getUser(rs);
                return user;
            }
        });
        return users;
    }

    private List<User> getAllUsersDetail() {
        List<User> users = jdbcTemplate.query(sqlGetAllUsers, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = getUser(rs);
                user.setPersonId(rs.getString("personId"));
                user.setUuid(rs.getString("uuid"));
                return user;
            }
        });
        return users;
    }

    private User getUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setSurname(rs.getString("surname"));
        return user;
    }

    private String getGeneratedUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private String validatedPersonId(String filename) throws FileNotFoundException {
        int personIdNum = 0;
        int numOfUsedId = getNumOfUsedId();

        String line = null;
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(filename)))) {
            while (numOfUsedId >= personIdNum) {
                line = scanner.nextLine();
                personIdNum++;
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Error during reading file " + this.filename + " " + e.getLocalizedMessage());
        }
        return line;
    }

    private int getNumOfUsedId() {
        if (getAllUsersDetail().isEmpty()) {
            return 0;
        } else {
            return (int) getAllUsersDetail().get(getAllUsersDetail().size() - 1).getId();
        }
    }
}