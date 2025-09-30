package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authDB = new HashMap<>();


    @Override
    public String createAuth(String userName) {
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, userName);
        authDB.put(token, authData);
        return token;
    }

    @Override
    public AuthData getAuth(String authToken) {
        if (authDB.containsKey(authToken)){
            return authDB.get(authToken);
        }
        else{
            return null;
        }

    }

    @Override
    public void deleteAuth(String authToken) {
        if (authDB.containsKey(authToken)){
            authDB.remove(authToken);
        }

    }

    @Override
    public void clear() {
        authDB.clear();
    }
}
