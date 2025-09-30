package dataaccess;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AuthSQLDBTest {
    AuthDAO authDAO;

    @BeforeEach
    void initiate(){
        Assertions.assertDoesNotThrow(() ->
        {
            authDAO = new MySQLAuthDAO();
        });
    }

    @AfterEach
    void wipe(){
        Assertions.assertDoesNotThrow(() ->
        {
            authDAO.clear();
        });
    }

    @Test
    @Order(1)
    void createAuthPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");

            Assertions.assertNotEquals(null, token);
        });
    }
    @Test
    @Order(2)
    void createAuthFail(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");
            AuthData authData = authDAO.getAuth(token);
            Assertions.assertEquals(authDAO.getAuth(token).username(), "john");
        });
    }


    @Test
    @Order(3)
    void getAuthPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");
            AuthData authData = authDAO.getAuth(token);
            Assertions.assertEquals(authData.username(), "john");
        });

    }

    @Test
    @Order(4)
    void getAuthFail(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");
            AuthData authData = authDAO.getAuth(token);
            Assertions.assertNotEquals(authData.username(), "craig");
        });
    }

    @Test
    @Order(5)
    void deleteAuthPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");
            authDAO.deleteAuth(token);
            Assertions.assertEquals(authDAO.getAuth(token), null);
        });


    }

    @Test
    @Order(6)
    void deleteAuthFail(){
        Assertions.assertDoesNotThrow(() ->
        {
            String token = authDAO.createAuth("john");
            AuthData authData = authDAO.getAuth(token);
            authDAO.deleteAuth(token);
            Assertions.assertNotEquals(authData.username(), authDAO.getAuth(token));
        });
    }



    @Test
    @Order(7)
    void clearAuthPass(){
        Assertions.assertDoesNotThrow(() ->
        {
            authDAO.clear();
        });
    }


}
