/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAL.DB;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Author: Carlo De Leon
 * Version: 1.0
 */
public class DbMSSQLConnectionProvider implements DAL.DB.IINTERFACE.IDbConnectionProvider {

    protected String host;
    protected String user;
    protected String password;
    protected String database;
    protected int port = 1433;
    private SQLServerDataSource ds;

    public DbMSSQLConnectionProvider() {
    }

    /**
     * Tries to connect to the database
     */
    public void connect() {
        ds = new SQLServerDataSource();
        ds.setServerName(getHost());
        ds.setDatabaseName(getDatabase());
        ds.setUser(getUser());
        ds.setPassword(getPassword());
        ds.setPortNumber(getPort());
    }

    /**
     * Gets the connection
     * @return the connection
     */
    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Gets the datasource
     * @return the datasource
     */
    public SQLServerDataSource getDataSource() {
        return ds;
    }

    /**
     * Gets the databse
     * @return the database
     */
    @Override
    public String getDatabase() {
        return database;
    }

    /**
     * Gets the host
     * @return the host
     */
    @Override
    public String getHost() {
        return host;
    }

    /**
     * Gets the user
     * @return the user
     */
    @Override
    public String getUser() {
        return user;
    }

    /**
     * Gets the password
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets the port
     * @return the port
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * Sets the database
     * @param database the new database
     */
    @Override
    public void setDatabase(String database) {
        if (database.isEmpty()) return;
        this.database = database;
    }

    /**
     * Sets the host
     * @param host the new host
     */
    @Override
    public void setHost(String host) {
        if (host.isEmpty()) return;
        this.host = host;
    }

    /**
     * Sets the user
     * @param user the new user
     */
    @Override
    public void setUser(String user) {
        if (user.isEmpty()) return;
        this.user = user;
    }

    /**
     * Sets the password
     * @param password the new password
     */
    @Override
    public void setPassword(String password) {
        if (password.isEmpty()) return;
        this.password = password;
    }

    /**
     * Sets the port
     * @param port the new port
     */
    @Override
    public void setPort(int port) {
        if (port <= 0) return;
        this.port = port;
    }

}
