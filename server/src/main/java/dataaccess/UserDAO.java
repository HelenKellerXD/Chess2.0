package dataaccess;

import model.UserData;

/**
 * run CRUD on this
 *  - create
 *  - read (get/find)
 *  - update
 *  - delete
 */
public interface UserDAO {

    /**
     * createUser: Create a new user.
     */
    void createUser(String username,String password, String email) throws DataAccessException;

    /**
     * getUser: Retrieve a user with the given username.
     * clearUsers: Deletes all UserData from the database
     */
    UserData getUser(String userName) throws DataAccessException;


    void clear() throws DataAccessException;
}
