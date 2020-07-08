package by.epam.tr.controller.command.ajax.impl;

import by.epam.tr.controller.command.Command;
import by.epam.tr.controller.constant_parameter.RequestParameterName;
import by.epam.tr.controller.util.GsonConverter;
import by.epam.tr.service.CityService;
import by.epam.tr.service.ServiceException;
import by.epam.tr.service.ServiceFactory;
import lombok.extern.log4j.Log4j2;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Log4j2
public class CountryAirport implements Command {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        CityService cityService = ServiceFactory.getInstance().getCityService();
        String country;
        List<String> countries;

        country = request.getParameter(RequestParameterName.COUNTRY);
        try {
            countries = cityService.cityWithAirportList(country);

            String countriesGson = GsonConverter.convertToGson(countries);
            response.getWriter().write(countriesGson);
        } catch (ServiceException e) {
            log.error("Cannot execute ajax command for country airport searching", e);
        } catch (IOException e) {
            log.error("Cannot write json to response", e);
        }
    }
}

