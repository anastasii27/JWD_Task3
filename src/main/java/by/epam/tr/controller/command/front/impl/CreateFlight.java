package by.epam.tr.controller.command.front.impl;

import by.epam.tr.bean.Flight;
import by.epam.tr.bean.User;
import by.epam.tr.controller.command.Command;
import by.epam.tr.controller.constant_parameter.RequestParameterName;
import by.epam.tr.service.FlightService;
import by.epam.tr.service.ServiceException;
import by.epam.tr.service.ServiceFactory;
import lombok.extern.log4j.Log4j2;
import static by.epam.tr.controller.util.RequestParametersExtractor.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;

@Log4j2
public class CreateFlight implements Command {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        FlightService flightService = ServiceFactory.getInstance().getFlightService();


        String flightNumber = request.getParameter(RequestParameterName.FLIGHT_NUMBER);
        String dispatcherFullName = request.getParameter(RequestParameterName.USER);
        String plane = request.getParameter(RequestParameterName.PLANE);
        LocalDate departureDate = LocalDate.parse(request.getParameter(RequestParameterName.DEPARTURE_DATE));
        LocalTime departureTime = LocalTime.parse(request.getParameter(RequestParameterName.DEPARTURE_TIME));
        String departureCountry = request.getParameter(RequestParameterName.DEPARTURE_COUNTRY);
        String departureCityWithAirport = request.getParameter(RequestParameterName.DEPARTURE_AIRPORT);
        LocalDate destinationDate = LocalDate.parse(request.getParameter(RequestParameterName.DESTINATION_DATE));
        LocalTime destinationTime = LocalTime.parse(request.getParameter(RequestParameterName.DESTINATION_TIME));
        String destinationCountry = request.getParameter(RequestParameterName.DESTINATION_COUNTRY);
        String destinationCityWithAirport = request.getParameter(RequestParameterName.DESTINATION_AIRPORT);


        User dispatcher = User.builder().name(userName(dispatcherFullName))
                                .surname(userSurname(dispatcherFullName)).build();

        Flight flight = Flight.builder().flightNumber(flightNumber)
                                        .dispatcher(dispatcher)
                                        .planeNumber(planeNumber(plane))
                                        .departureDate(departureDate)
                                        .departureTime(departureTime)
                                        .departureCountry(departureCountry)
                                        .departureAirportShortName(airportName(departureCityWithAirport))
                                        .destinationDate(destinationDate)
                                        .destinationTime(destinationTime)
                                        .destinationCountry(destinationCountry)
                                        .destinationAirportShortName(airportName(destinationCityWithAirport)).build();
        try {
            int k = flightService.createFlight(flight);

        } catch (ServiceException e) {
            log.error("Cannot execute command for flight creating", e);
            errorPage(response);
        }
    }
}