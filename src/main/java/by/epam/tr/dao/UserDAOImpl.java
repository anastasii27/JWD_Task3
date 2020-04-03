package by.epam.tr.dao;

import by.epam.tr.bean.Group;
import by.epam.tr.bean.User;
import by.epam.tr.dao.connectionpool.ConnectionPool;
import by.epam.tr.dao.connectionpool.ConnectionPoolException;
import java.sql.*;
import java.util.ArrayList;

public class UserDAOImpl implements UserDAO {

    private final static String INSERT =  "INSERT INTO airport.users (`role-id`, login, `password`, `name`, surname, email, `career-start-year`) VALUES((SELECT id FROM airport.roles WHERE title = ?),?,?,?,?,?,?);";
    private final static String SELECT_USER_INFO =  "SELECT title AS `user-role` ,login ,`name`, surname, email, `career-start-year` FROM airport.users JOIN airport.roles ON roles.id = users.`role-id`;";
    private final static String SELECT_FOR_SING_IN = "SELECT title  AS `role` , `name`, surname, email, `career-start-year` FROM users JOIN roles ON users.`role-id` = roles.id WHERE login = ? AND `password`=?;  ";
    private final static String SELECT_USER_GROUPS = " SELECT `short-name`, `date-of-creating` FROM `flight-teams-m2m-users` JOIN users ON users.id =  `flight-teams-m2m-users`.`user-id` JOIN `flight-teams` ON `flight-teams-m2m-users`.`flight-team-id` = `flight-teams`.id WHERE users.login = ?;";
    private final static String CHECK_USER_EXISTENCE = "SELECT `name` FROM users WHERE login = ?";
    @Override
    public boolean addNewUser(User user, String login, String password) throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;
        boolean flag = false;

        try {
            pool = ConnectionPool.getInstance();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(INSERT);

            ps.setString(1,user.getRole());
            ps.setString(2,login);
            ps.setString(3,password);
            ps.setString(4,user.getName());
            ps.setString(5,user.getSurname());
            ps.setString(6,user.getEmail());
            ps.setInt(7,user.getCareer_start_year());

            ps.executeUpdate();
            flag = true;

        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during inserting operation");
        }finally {

            if(ps != null)
                closeStatement(ps);

            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return flag;
    }

    @Override
    public User singIn(String login, String password) throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;
        User user;

        try {
            pool = ConnectionPool.getInstance();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(SELECT_FOR_SING_IN);

            ps.setString(1, login);
            ps.setString(2,password);

            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                return null;
            }

            user =  new User(rs.getString("role"),rs.getString("name"), rs.getString("surname"), rs.getString("email"),rs.getInt("career-start-year"));


        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during select operation!");
        }finally {

            if(ps != null){
                closeStatement(ps);
            }

            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return user;
    }

    @Override
    public ArrayList<User> allUsersInfo() throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList <User> users = new ArrayList<>();

        try {

            pool = ConnectionPool.getInstance();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(SELECT_USER_INFO);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                users.add(new User(rs.getString("user-role"),rs.getString("name"),rs.getString("surname"),
                        rs.getString("email"), rs.getInt("career-start-year")));
            }

        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during select operation!");
        }finally{
            if(ps != null){
                closeStatement(ps);
            }

            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return users;
    }


    @Override
    public ArrayList<Group> userGroups(String login) throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ArrayList <Group> groups = new ArrayList<>();

        try {
            pool = ConnectionPool.getInstance();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(SELECT_USER_GROUPS);

            ps.setString(1,login);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                groups.add(new Group(rs.getString("short-name"), rs.getString("date-of-creating")));
            }


        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during select operation!");
        }finally{
            if(ps != null){
                closeStatement(ps);
            }

            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return groups;
    }

    @Override
    public boolean doesUserExist(String login) throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            pool = ConnectionPool.getInstance();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(CHECK_USER_EXISTENCE);

            ps.setString(1, login);

            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                return false;
            }


        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during select operation!");
        }finally {

            if(ps != null){
                closeStatement(ps);
            }
            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return true;
    }

//    @Override
//    public ArrayList<String> getUserFlights(String login) {
//        return null;
//    }

    private static void closeStatement(PreparedStatement ps) throws DAOException  {
        try {
            ps.close();
        } catch (SQLException e) {
           //
        }
    }

}

