package client;

import org.junit.jupiter.api.*;
import request.LoginRequest;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);

    }

    @BeforeEach
    void clearDatabase() {
        facade.clear();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    // ----------- REGISTER ---------- //

    @Test
    @DisplayName("PASS - register correct")
    @Order(1)
    void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    @DisplayName("FAIL - register twice")
    @Order(2)
    void registerTwice() throws Exception{
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows( Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
        }
        );
    }

    // ----------- LOGIN ---------- //


    @Test
    @DisplayName("PASS - log into account")
    @Order(3)
    void loginSuccess() throws Exception{
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(facade.login(new LoginRequest("player1", "password")));

    }



        @Test
    @DisplayName("FAIL - try logging in to non-existant user")
    @Order(4)
        void loginNonUser() throws Exception{

        }


    // ----------- LOGOUT ---------- //


    @Test
    @DisplayName("PASS - log out")
    @Order(5)
    void logoutSuccess() throws Exception{

    }

    @Test
    @DisplayName("FAIL - try logging out before logging in")
    @Order(6)
    void logOutWithoutLogin() throws Exception{

    }


    // ----------- LIST_GAMES ---------- //


    @Test
    @DisplayName("PASS - list games")
    @Order(7)
    void listGamesSuccess() throws Exception{

    }

    @Test
    @DisplayName("FAIL - try listing games before logging in")
    @Order(8)
    void listGamesWithoutLogin() throws Exception{

    }


    // ----------- CREATE_GAMES ---------- //


    @Test
    @DisplayName("PASS - create a game")
    @Order(9)
    void createGameSuccess() throws Exception{

    }

    @Test
    @DisplayName("FAIL - try creating a game before logging in")
    @Order(10)
    void createGameWithoutLogin() throws Exception{

    }


    // ----------- JOIN_GAME ---------- //


    @Test
    @DisplayName("PASS - join a game")
    @Order(11)
    void joinGameSuccess() throws Exception{

    }

    @Test
    @DisplayName("FAIL - try to join a game that doesnt exist")
    @Order(12)
    void joinGameNoExist() throws Exception{

    }


    // ----------- CLEAR ---------- //

    @Test
    @DisplayName("PASS - Database is emptied")
    @Order(13)
    void clearSuccess() throws Exception{

    }



}
