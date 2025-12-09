package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import passoff.model.TestCreateRequest;
import passoff.model.TestUser;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;


// do one positive and one negative test case

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    private static TestUser existingUser;
    private static TestUser newUser;
    private static TestCreateRequest createRequest;
    private String existingAuth;
    public GameService gameService;
    public UserService userService;

    // ### TESTING SETUP/CLEANUP ###

    @AfterEach
    public void clear() throws DataAccessException {
        gameService.clear();
        userService.clear();
    }


    @BeforeEach
    public void setup() {
        userService = new UserService();
        gameService = new GameService();
    }

    // ### SERVICE-LEVEL TESTS ###

    /**
     * Register tests - Pass and Fail
     */

    @Test
    @Order(1)
    @DisplayName("User Service - Register PASS")
    public void registerSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        Assertions.assertEquals(registerRequest.username(), registerResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(registerResult.authToken(),
                "Response did not return authentication String");
    }

    @Test
    @Order(2)
    @DisplayName("User Service - Register FAIL (user already exists)")
    public void registerFail() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "username is already in use");
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(registerRequest),
                "double register exception was not thrown");

        Assertions.assertEquals(registerRequest.username(), registerResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(registerResult.authToken(),
                "Response did not return authentication String");
    }

    /**
     * Login tests - Pass and Fail
     */
    @Test
    @Order(3)
    @DisplayName("User Service - Login PASS")
    public void loginSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        LoginRequest loginRequest = new LoginRequest("dave", "dave's password");
        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> userService.login(loginRequest),
                "login() threw an exception");

        Assertions.assertEquals(loginRequest.username(), loginResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(loginResult.authToken(),
                "Response did not return authentication String");
    }

    @Test
    @Order(4)
    @DisplayName("User Service - Login FAIL (incorrect password)")
    public void loginFail() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        LoginRequest loginRequest = new LoginRequest("dave", "NOT dave's password");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(loginRequest),
                "password did not throw exception despite being incorrect");

        Assertions.assertEquals(registerRequest.username(), registerResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(registerResult.authToken(),
                "Response did not return authentication String");
    }


    /**
     * Logout tests - Pass and Fail
     */
    @Test
    @Order(5)
    @DisplayName("User Service - Logout PASS")
    public void logoutSuccess() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        LoginRequest loginRequest = new LoginRequest("dave", "dave's password");
        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> userService.login(loginRequest),
                "login() threw an exception");

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        Assertions.assertDoesNotThrow(() -> userService.login(loginRequest),
                "logout() threw an exception");

        Assertions.assertEquals(loginRequest.username(), loginResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(loginResult.authToken(),
                "Response did not return authentication String");
    }

    @Test
    @Order(5)
    @DisplayName("User Service - Logout FAIL")
    public void logoutFail() throws DataAccessException {
        //register
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        //login
        LoginRequest loginRequest = new LoginRequest("dave", "dave's password");
        LoginResult loginResult = Assertions.assertDoesNotThrow(() -> userService.login(loginRequest),
                "login() threw an exception");

        //logout
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        Assertions.assertDoesNotThrow(() -> userService.logout(logoutRequest),
                "logout() threw an exception");
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest),
                "logout() threw an exception");


        Assertions.assertEquals(loginRequest.username(), loginResult.username(),
                "Response did not give the same username as user");
        Assertions.assertNotNull(userService.userDAO.getUser("dave"),
                "User was not found in User Database");
        Assertions.assertNotNull(loginResult.authToken(),
                "Response did not return authentication String");
    }

    /**
     * Create Game tests - Pass and Fail (fail can d
     */
    @Test
    @Order(6)
    @DisplayName("Game Service - Create Game PASS")
    public void createGameSuccess() throws DataAccessException {

        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest("dave's game", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(()-> gameService.createGame(createGameRequest),
                "createGame() threw an exception");

        //check to see if game was stored in gameDAO
        Assertions.assertNotNull(gameService.gameDAO.getGame(createGameResult.gameID()),
                "User was not found in User Database");
    }
    @Test
    @Order(7)
    @DisplayName("Game Service - Create Game FAIL")
    public void createGameFail() {

        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest(null, "authToken");
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.createGame(createGameRequest),
                "createGame() didn't throw an exception");

    }


    /**
     * Join Game tests - Pass and Fail
     */

    @Test
    @Order(8)
    @DisplayName("Game Service - Join Game Success")
    public void joinGameSuccess() {
        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest("dave's game", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest),
                "createGame() threw an exception");

        //join game
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), "dave");
        Assertions.assertDoesNotThrow(()-> gameService.joinGame(joinGameRequest));
    }

    @Test
    @Order(9)
    @DisplayName("Game Service - Join Game Fail(wrong color, double join, bad ID)")
    public void joinGameFail() {
        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest("dave's game", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest),
                "createGame() threw an exception");

        //join game [bad color, double join, bad ID]
        int gameID = createGameResult.gameID();
        //-> bad color
        JoinGameRequest joinGameRequestColor = new JoinGameRequest("Green", gameID, "dave");
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.joinGame(joinGameRequestColor),
                "Bad color was not thrown");
        //-> double join
        JoinGameRequest joinGameRequestDouble = new JoinGameRequest("WHITE", gameID, "dave");
        try {
            gameService.joinGame(joinGameRequestDouble);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.joinGame(joinGameRequestDouble),
                "Double join was not thrown");
        //-> badID
        JoinGameRequest joinGameRequestgameID = new JoinGameRequest("WHITE", gameID+1, "dave");
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.joinGame(joinGameRequestDouble),
                "Bad gameID was not thrown");

    }



    /**
     * List Games tests - Pass and Fail
     */
    @Test
    @Order(10)
    @DisplayName("Game Service - List Games PASS")
    public void listGamesSuccess() {
        //create game
        CreateGameRequest createGameRequest1 = new CreateGameRequest("dave's game 1", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult1 = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest1),
                "createGame() threw an exception");

        //create game 2
        CreateGameRequest createGameRequest2 = new CreateGameRequest("dave's game 2", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult2 = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest2),
                "createGame() threw an exception");

        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest("dave's game 3", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult3 = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest),
                "createGame() threw an exception");

        //list games
        ListGamesResult gamesList = Assertions.assertDoesNotThrow(()-> gameService.listGames());
        Assertions.assertNotNull(gamesList.games(), "gameDAO is empty");
        Assertions.assertEquals(gamesList.games().size(), 3, "gameDAO does not have 3");

    }

    @Test
    @Order(11)
    @DisplayName("Game Service - List Games FAIL(Authtoken included in parameter)")
    public void listGamesFail() {
        //create game
        CreateGameRequest createGameRequestA = new CreateGameRequest("dave's game A", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResultA = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequestA),
                "createGame() threw an exception");

        //create game 2
        CreateGameRequest createGameRequestB = new CreateGameRequest("dave's game B", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResultB = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequestB),
                "createGame() threw an exception");

        //create game
        CreateGameRequest createGameRequestC = new CreateGameRequest("dave's game C", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResultC = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequestC),
                "createGame() threw an exception");

        ListGamesRequest listGamesRequest = new ListGamesRequest("authToken");
        //list games
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.listGames(),
                "parameter was used but no error thrown");

        ListGamesResult gamesList = Assertions.assertDoesNotThrow(()-> gameService.listGames());
        Assertions.assertNotNull(gamesList.games(), "gameDAO is empty");
        Assertions.assertNotEquals(gamesList.games().size(), 4, "gameDAO has 4 games");

    }

    /**
     * Clear tests - Pass and Fail
     */

    @Test
    @Order(12)
    @DisplayName("Both Service - Clear PASS")
    public void clearSuccess() throws DataAccessException {
        //create user
        RegisterRequest registerRequest = new RegisterRequest("dave", "dave's password", "dave's email");
        RegisterResult registerResult = Assertions.assertDoesNotThrow(() -> userService.register(registerRequest),
                "register() threw an exception");

        //create game
        CreateGameRequest createGameRequest1 = new CreateGameRequest("dave's game 1", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult1 = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest1),
                "createGame() threw an exception");

        //create game 2
        CreateGameRequest createGameRequest2 = new CreateGameRequest("dave's game 2", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult2 = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest2),
                "createGame() threw an exception");

        //create game
        CreateGameRequest createGameRequest = new CreateGameRequest("dave's game", "authToken");
        //check that no exception was thrown
        CreateGameResult createGameResult = Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest),
                "createGame() threw an exception");

        gameService.clear();
        userService.clear();

        Assertions.assertNull(userService.userDAO.getUser(registerRequest.username()), "list is not empty");
        Assertions.assertNull(userService.authDAO.getAuth(registerResult.authToken()), "list is not empty");
        Assertions.assertEquals(0, gameService.gameDAO.listGames().size(), "list is not empty");



    }






    }

