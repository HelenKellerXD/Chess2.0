package dataaccess;


import chess.ChessGame;
import model.AuthData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserSQLDBTest {
    UserDAO userDAO;

    @BeforeEach
    void initiate(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO = new MySQLUserDAO();
        });
    }

    @AfterEach
    void wipe(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO.clear();
        });
    }

    @Test
    @Order(1)
    void createUserPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO.createUser("john", "password", "email");

        });
    }
    @Test
    @Order(2)
    void createUserFail(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO.createUser("john", "password", "email");
        });
        Assertions.assertThrows(DataAccessException.class, () ->{
            userDAO.createUser("john", "password", "email");
        });
    }

    @Test
    @Order(3)
    void getUserPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO.createUser("john", "password", "email");
            Assertions.assertNotNull(userDAO.getUser("john"));
        });

    }

    @Test
    @Order(4)
    void getUserFail(){
        Assertions.assertDoesNotThrow(() ->
        {
            Assertions.assertNull(userDAO.getUser("john"));
            userDAO.createUser("john", "password", "email");
            Assertions.assertNull(userDAO.getUser("davy"));

        });
    }

    @Test
    @Order(5)
    void clearAuthPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            userDAO.clear();
        });
    }

}
