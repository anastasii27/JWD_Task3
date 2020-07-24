package by.epam.airport_system.controller.command.front.impl;

import by.epam.airport_system.bean.Flight;
import by.epam.airport_system.bean.User;
import by.epam.airport_system.controller.command.Command;
import by.epam.airport_system.controller.constant_parameter.JSPPageName;
import by.epam.airport_system.controller.constant_parameter.RequestParameterName;
import by.epam.airport_system.controller.util.RequestToMapParser;
import by.epam.airport_system.controller.util.ResponseMessageManager;
import by.epam.airport_system.service.FlightService;
import by.epam.airport_system.service.ServiceException;
import by.epam.airport_system.service.ServiceFactory;
import by.epam.airport_system.service.validation.ValidationFactory;
import by.epam.airport_system.service.validation.ValidationResult;
import by.epam.airport_system.service.validation.Validator;
import lombok.extern.log4j.Log4j2;
import static by.epam.airport_system.controller.util.RequestParametersExtractor.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Log4j2
public class CreateFlight implements Command {
    private static final String ANSWER = "local.message.create_flight.1";

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);

        try {
            List<String> validationResults = initialValidation(request, response);

            if(validationResults.size() == 0){
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

                FlightService flightService = ServiceFactory.getInstance().getFlightService();
                boolean operationResult = flightService.createFlight(flight);

                if(operationResult){
                    session.setAttribute(RequestParameterName.FLIGHT, flight);
                    response.sendRedirect(request.getContextPath()+ "/airport?action=free_crews_for_flight");
                }else {
                    String language = String.valueOf(session.getAttribute(RequestParameterName.LOCAL));
                    ResponseMessageManager resourceManager = new ResponseMessageManager(language);

                    session.setAttribute(RequestParameterName.RESULT_INFO,resourceManager.getValue(ANSWER));
                    response.sendRedirect(JSPPageName.RESULT_PAGE);
                }
            }
        } catch(ServiceException | IOException e){
                log.error("Cannot execute command for flight creating", e);
                errorPage(response);
        }
    }

    private List<String> initialValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Validator validator = ValidationFactory.getInstance().getCreatedFlightValidation();

        Map<String, String> params = RequestToMapParser.toRequestParamsMap(request);
        ValidationResult result = validator.validate(params);

        if(!result.isEmpty()){
            request.getSession().setAttribute(RequestParameterName.RESULT_INFO, result.getErrorsList());
            response.sendRedirect(JSPPageName.RESULT_PAGE);
        }
        return result.getErrorsList();
    }
}