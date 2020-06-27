package by.epam.tr.dao.impl;

import by.epam.tr.bean.Flight;
import by.epam.tr.dao.CloseOperation;
import by.epam.tr.dao.DAOException;
import by.epam.tr.dao.FlightDAO;
import by.epam.tr.dao.connectionpool.ConnectionPool;
import by.epam.tr.dao.connectionpool.ConnectionPoolException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FlightDAOImpl extends CloseOperation implements FlightDAO {

    private final static String ARRIVAL = "arrival";
    private final static String DEPARTURE = "departure";
    private final static String SELECT_AIRPORT_DEPARTURE = "SELECT `flight-number`, `departure-date` AS `date`, `departure-time` AS `time`, a1.`name` AS `airport`,\n" +
                                                "cities.`name` AS `city`, a1.`name-abbreviation` AS `airport-short-name`, model, `status`\n" +
                                                "FROM flights\n" +
                                                "JOIN airports AS a1 ON  a1.id = flights.`destination-airport-id`\n" +
                                                "JOIN cities ON cities.id = (SELECT `city-id` FROM airports WHERE airports.`name` = a1.`name`)\n" +
                                                "JOIN planes ON  planes.id = `plane-id`\n" +
                                                "JOIN airports AS a2 ON a2.id = flights.`departure-airport-id`\n" +
                                                "WHERE `departure-date` = ? AND a2.`name-abbreviation` = ?;";

    private final static String SELECT_AIRPORT_ARRIVAL = "SELECT `flight-number`, `departure-date` AS `date`, `destination-time` AS `time`,cities.`name` AS `city`,\n" +
                                                "a2.`name-abbreviation` AS `airport-short-name`, model, `status`\n" +
                                                "FROM flights\n" +
                                                "JOIN airports AS a2 ON \ta2.id = flights.`departure-airport-id`\n" +
                                                "JOIN cities ON cities.id = (SELECT `city-id` FROM airports WHERE airports.`name` = a2.`name`)\n" +
                                                "JOIN airports AS a1 ON  a1.id = flights.`destination-airport-id`\n" +
                                                "JOIN planes ON  planes.id = `plane-id`\n" +
                                                "WHERE `destination-date` = ? AND a1.`name-abbreviation` = ?;";

    private final static String SELECT_FLIGHT_INFO =   "SELECT `status`,`destination-date`, `destination-time`, a2.`name` AS `departure-airport`, c2.`name` AS `departure-city`, cnt2.`name` AS `departure-country`,  a2.`name-abbreviation` AS `dep-airport-short-name`, \n" +
                                                "`departure-date`, `departure-time`, a1.`name` AS `destination-airport`, c1.`name` AS `destination-city` , cnt1.`name` AS `destination-country`, a1.`name-abbreviation` AS `dest-airport-short-name`\n" +
                                                "FROM flights\n" +
                                                "JOIN airports AS a1 ON  a1.id = flights.`destination-airport-id`\n" +
                                                "JOIN cities AS c1 ON c1.id = (SELECT `city-id` FROM airports WHERE airports.`name` = a1.`name`)\n" +
                                                "JOIN countries AS cnt1 ON cnt1.id = c1.`country-id`\n" +
                                                "JOIN airports AS a2 ON \ta2.id = flights.`departure-airport-id`\n" +
                                                "JOIN cities AS c2 ON c2.id = (SELECT `city-id` FROM airports WHERE airports.`name` = a2.`name`)\n" +
                                                "JOIN countries AS cnt2 ON cnt2.id = c2.`country-id`\n" +
                                                "WHERE `flight-number` = ? AND `departure-date` = ?;\n";

    private final static String CREATE_FLIGHT = "INSERT INTO airport.flights (`flight-number`, `plane-id`, `flight-team-id`, `departure-airport-id`," +
                                                " `destination-airport-id`, `departure-date`,\n" +
                                                " `destination-date`, `departure-time`, `destination-time`, `status`, `dispatcher-id`) \n" +
                                                " VALUES (?, (SELECT id FROM planes WHERE model = ?), (SELECT id FROM `flight-teams` WHERE `short-name` = ?),\n" +
                                                "(SELECT id FROM airports WHERE `name-abbreviation` = ?), (SELECT id FROM airports WHERE `name-abbreviation` = ?), ?, ?,\n" +
                                                "?, ?, ?, (SELECT id FROM users WHERE `name` = ? AND surname = ?));";

    @Override
    public List<Flight> flightsByDay(Map<String, String> params) throws DAOException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List <Flight> flights = new ArrayList<>();
        String query;

        try {
            connection = pool.takeConnection();
            query = dbQueryByFlightType(params.get("type"));

            if(query!=null) {
                ps = connection.prepareStatement(query);

                ps.setDate(1, Date.valueOf(params.get("departure_date")));
                ps.setString(2, params.get("airport_short_name"));

                rs = ps.executeQuery();
                while (rs.next()) {
                    flights.add( Flight.builder().status(rs.getString("status"))
                                                .planeModel(rs.getString("model"))
                                                .departureDate(rs.getDate("date").toLocalDate())
                                                .departureTime(rs.getTime("time").toLocalTime())
                                                .destinationCity(rs.getString("city"))
                                                .destinationAirportShortName(rs.getString("airport-short-name"))
                                                .flightNumber(rs.getString("flight-number")).build());
                }
            }
        } catch (ConnectionPoolException | SQLException e) {
            throw new DAOException("Exception during departures/arrivals selecting!", e);
        }finally{
            closeAll(rs, ps, pool, connection);
        }
        return flights;
    }

    private String dbQueryByFlightType(String flightsType) {//todo заменить
        if (flightsType.equals(ARRIVAL)) {
            return SELECT_AIRPORT_ARRIVAL;
        }

        if (flightsType.equals(DEPARTURE)) {
            return SELECT_AIRPORT_DEPARTURE;
        }
        return null;
    }

    @Override
    public Flight flightInfo(String flightNumber, String departureDate) throws DAOException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Flight flight;

        try {
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(SELECT_FLIGHT_INFO);

            ps.setString(1,flightNumber);
            ps.setString(2,departureDate);

            rs = ps.executeQuery();
            if(!rs.next()){
                return null;
            }
            flight = Flight.builder().status(rs.getString("status"))
                    .destinationDate(rs.getDate("destination-date").toLocalDate())
                    .destinationTime( rs.getTime("destination-time").toLocalTime())
                    .destinationAirport( rs.getString("destination-airport"))
                    .destinationCity(rs.getString("destination-city"))
                    .destinationCountry(rs.getString("destination-country"))
                    .destinationAirportShortName(rs.getString("dest-airport-short-name"))
                    .departureDate(rs.getDate("departure-date").toLocalDate())
                    .departureTime(rs.getTime("departure-time").toLocalTime())
                    .departureAirport( rs.getString("departure-airport"))
                    .departureCity( rs.getString("departure-city"))
                    .departureCountry(rs.getString("departure-country"))
                    .departureAirportShortName( rs.getString("dep-airport-short-name")).build();

        } catch (ConnectionPoolException | SQLException e) {
            throw new DAOException("Exception during flight info selecting!", e);
        }finally{
            closeAll(rs, ps, pool, connection);
        }
        return flight;
    }



    @Override
    public int createFlight(Flight flight) throws DAOException {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = pool.takeConnection();
            ps =  connection.prepareStatement(CREATE_FLIGHT);

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getPlaneModel());
            ps.setString(3, "crewName");
            ps.setString(4, flight.getDepartureAirport());
            ps.setString(5, flight.getDestinationAirport());
            ps.setDate(6, Date.valueOf(flight.getDepartureDate()));
            ps.setDate(7, Date.valueOf(flight.getDestinationDate()));
            ps.setTime(8, Time.valueOf(flight.getDepartureTime()));
            ps.setTime(9, Time.valueOf(flight.getDestinationTime()));
            ps.setString(10, flight.getStatus());
            ps.setString(11, "User");
            ps.setString(12, "User");

            return ps.executeUpdate();
        } catch (ConnectionPoolException | SQLException e) {
            throw new DAOException("Exception during nearest flight selecting!", e);
        }finally{
            closeAll(ps, pool, connection);
        }
    }
}