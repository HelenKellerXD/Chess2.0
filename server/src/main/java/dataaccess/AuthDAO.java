package dataaccess;

import model.AuthData;

/**
 * run CRUD on this
 *  - create
 *  - read (get/find)
 *  - update
 *  - delete
 */
public interface AuthDAO {

    /**
     * createAuth: Create a new authorization.
     */
    String createAuth(String userName) throws DataAccessException;


    /**
     * getAuth: Retrieve an authorization given an authToken.
     * deleteAuth: Delete an authorization so that it is no longer valid.
     */
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
