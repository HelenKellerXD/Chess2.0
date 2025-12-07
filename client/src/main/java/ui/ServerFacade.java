package ui;

import com.google.gson.Gson;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import result.RegisterResult;

public class ServerFacade {
    /**
     * so this just copies the api endpoint calls and use the info from the http video training
     */

    private final String serverURL;
    private String authToken;
    private String username;
    private final Gson gson = new Gson();
    private ClientCommunicator clientCommunicator;


    public ServerFacade(String serverURL){
        this.serverURL = serverURL;
        clientCommunicator = new ClientCommunicator(serverURL);

    }
    public ServerFacade(int portNum){
        this("http://localhost:" + portNum);
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        var path = "/user";
        return clientCommunicator.makeRequest("POST",path,request,RegisterResult.class, null);

    }
    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username,password,email);
        return register(request);
    }


        public LoginResult login(LoginRequest request)throws Exception{
        var path = "/session";
        LoginResult result = clientCommunicator.makeRequest("POST",path, request, LoginResult.class, null);
        this.authToken = result.authToken();
        this.username = request.username();
        return result;

    }
    public void logout(LogoutRequest request)throws Exception{
        var path = "/session";
        clientCommunicator.makeRequest("DELETE",path,request, null, authToken);
        this.authToken = null;

    }
    public ListGamesResult listGames() throws Exception {
        var path = "/game";
        return clientCommunicator.makeRequest("GET", path, null, ListGamesResult.class, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest request)throws Exception{
        var path = "/game";
        return clientCommunicator.makeRequest("POST",path, request, CreateGameResult.class, authToken);

    }
    public void joinGame(JoinGameRequest request)throws Exception{
        var path = "/game";
        clientCommunicator.makeRequest("PUT",path, request, null, authToken);

    }
    public void clear(){
        var path = "/db";
        try{
            clientCommunicator.makeRequest("DELETE",path, null, null, null);
        }
        catch (Exception e){

        }

    }

    public String getAuthToken(){
        return authToken;
    }

    public void setAuth(String authToken, String username){
        this.authToken = authToken;
        this.username = username;
    }

    /// UPDATE ----> pass in a game to update the game database
    public String updateGame(){
        return authToken;
    }


    public String getUsername(){
        return username;
    }

}
