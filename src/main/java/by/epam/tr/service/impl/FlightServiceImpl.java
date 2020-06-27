package by.epam.tr.service.impl;

import by.epam.tr.bean.Flight;
import by.epam.tr.dao.DAOException;
import by.epam.tr.dao.DAOFactory;
import by.epam.tr.dao.FlightDAO;
import by.epam.tr.service.FlightService;
import by.epam.tr.service.ServiceException;
import by.epam.tr.service.validation.ValidationFactory;
import by.epam.tr.service.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlightServiceImpl implements FlightService {
    private FlightDAO dao = DAOFactory.getInstance().getFlightDAO();
    private Validator dateValidation = ValidationFactory.getInstance().getDateValidation();

    @Override
    public List<Flight> flightsByDay(Map<String, String> params) throws ServiceException {
        List<Flight> flights;

        if(dateValidation.validate(params).size()!= 0){
            return Collections.emptyList();
        }
        try {
            flights =  dao.flightsByDay(params);
        } catch (DAOException e) {
            throw new ServiceException("Exception during getting arrivals/departures", e);
        }
        return flights;
    }

    @Override
    public Flight flightInfo(String flightNumber, String departureDate) throws ServiceException {
        Flight flight;
        try {
            flight =  dao.flightInfo(flightNumber,  departureDate);
        } catch (DAOException e) {
            throw new ServiceException("Exception during getting flight info", e);
        }
        return flight;
    }


}
