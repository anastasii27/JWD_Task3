package by.epam.tr.dao.impl;

import by.epam.tr.bean.Flight;
import by.epam.tr.dao.DAOException;
import by.epam.tr.dao.FlightDAO;
import by.epam.tr.dao.connectionpool.ConnectionPool;
import by.epam.tr.dao.connectionpool.ConnectionPoolException;
import java.sql.*;
import java.util.ArrayList;

public class FlightDAOImpl implements FlightDAO {

    private final static String USER_FLIGHT =   "select model, `departure-date`, `departure-time`, `destination-date`, `destination-time`,  "+
                                                "a1.`name` as `destination-airport`, c1.`name` as `destination-city` , cnt1.`name` as `destination-country`,  a2.`name` as `departure-airport`, "+
                                                "c2.`name` as `departure-city`, cnt2.`name` as `departure-country`from flights "+
                                                "join `planes-characteristic` on (select `planes-characteristic-id` from planes where planes.id = `plane-id` ) = `planes-characteristic`.id "+
                                                "join `flight-teams` on flights.`flight-team-id` = `flight-teams`.id "+
                                                "join `flight-teams-m2m-users` on   `flight-teams-m2m-users`.`flight-team-id` = `flight-teams`.id "+
                                                "join airports as a1 on  a1.id = flights.`destination-airport-id` "+
                                                "join cities as c1 on c1.id = (select  `city-id` from airports where airports.`name` = a1.`name`)"+
                                                "join countries as cnt1 on cnt1.id = c1.`country-id`"+
                                                "join airports as a2 on 	a2.id = flights.`departure-airport-id` "+
                                                "join cities as c2 on c2.id = (select  `city-id` from airports where airports.`name` = a2.`name`) "+
                                                "join countries as cnt2 on  cnt2.id = c2.`country-id`"+
                                                "where `user-id` = (select id from users where login = ?);";

    @Override
    public ArrayList<Flight> userFlightsList(String login) throws DAOException {

        ConnectionPool pool = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList <Flight> flights = new ArrayList<>();

        try {

            pool = ConnectionPool.getInstance();
            pool.poolInitialization();
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(USER_FLIGHT);

            ps.setString(1,login);

            rs = ps.executeQuery();

            while (rs.next()){
                flights.add(new Flight(rs.getString("model"), rs.getString("departure-date"), rs.getString("departure-time"), rs.getString("destination-date"),
                        rs.getString("destination-time"), rs.getString("destination-airport"), rs.getString("destination-city"), rs.getString("destination-country"),
                        rs.getString("departure-airport"), rs.getString("departure-city"), rs.getString("departure-country")));
            }


        } catch (ConnectionPoolException e) {
            throw new DAOException("Exception during taking connection!");
        } catch (SQLException e) {
            throw new DAOException("Exception during flight selecting!");
        }finally{

            if(rs !=  null){
                closeResultSet(rs);
            }

            if(ps != null){
                closeStatement(ps);
            }

            if (pool != null) {
                pool.releaseConnection(connection);
            }
        }

        return flights;
    }

    @Override
    public ArrayList<Flight> allFlightsList() {
        return null;
    }


    private void closeStatement(Statement st) {

        try {
            st.close();
        } catch (SQLException e) {
            //log
        }
    }

    private void closeResultSet(ResultSet rs){

        try {
            rs.close();
        } catch (SQLException e) {
            //log
        }
    }


}