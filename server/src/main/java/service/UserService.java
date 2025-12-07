package service;

import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService() {

        try {
            userDAO = new MySQLUserDAO();
            authDAO = new MySQLAuthDAO();
            //System.out.println("SQL User and Auth database");

        } catch (DataAccessException e) {
            userDAO = new MemoryUserDAO();
            authDAO = new MemoryAuthDAO();
            System.out.println("local User and Auth database");
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {

        if (userDAO.getUser(registerRequest.username()) != null){
            // if user already exists, throw 403 error
            throw new DataAccessException("Username: " + registerRequest.username() + " already exists.");
        } else {
            // if user doesn't exist then create user and token
            userDAO.createUser(registerRequest.username(),registerRequest.password(), registerRequest.email());
            String authToken = authDAO.createAuth(registerRequest.username());
            // return username, token combo
            return new RegisterResult(registerRequest.username(), authToken);
        }
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{


        //Check to see if username exists in userDB
        if (userDAO.getUser(loginRequest.username()) == null){
            //if user doesn't exist -> throw exception (username not found)
            throw new DataAccessException("Username: " + loginRequest.username() + " doesn't exist.");
        }
        //Check to see if login password matches database password
        /// Update: compare hashedpassword
        /// update - variables added due to hashing performed by sql and BCrypt
        String loginPassword = loginRequest.password();
        String hashedStoredPassword = userDAO.getUser(loginRequest.username()).password();
        if (!BCrypt.checkpw(loginPassword,hashedStoredPassword)){
            //if passwords don't match -> throw exception (password incorrect)
            throw new DataAccessException("Password: " + loginRequest.password() + " incorrect");
        }
        //create auth token
        String token = authDAO.createAuth(loginRequest.username());
        //return loginResult (username, authToken)
        return new LoginResult(loginRequest.username(), token);

    }


    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        // check to see if user authToken is even present in the authDAO
        if(authDAO.getAuth(logoutRequest.authToken()) == null){
            // if token not present -> throw exception
            throw new DataAccessException("User is already logged out");
        }
        // delete auth token from authDAO
        authDAO.deleteAuth(logoutRequest.authToken());
    }

    public void validateToken(String authToken) throws DataAccessException{
        // check to see if user authToken is even present in the authDAO
        if(authDAO.getAuth(authToken) == null){
            // if token not present -> throw exception
            throw new DataAccessException("invalid token");
        }
    }

    public String getUsername(String authToken) throws DataAccessException{
        // check to see if user authToken is even present in the authDAO
        if(authDAO.getAuth(authToken) == null){
            // if token not present -> throw exception
            throw new DataAccessException("invalid token");
        }
        return authDAO.getAuth(authToken).username();
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }
}
