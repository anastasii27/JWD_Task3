package by.epam.tr.controller.command.front.impl;

import by.epam.tr.controller.command.Command;
import by.epam.tr.controller.constant_parameter.JSPPageName;
import by.epam.tr.controller.constant_parameter.RequestParameterName;
import by.epam.tr.service.ListCreatorService;
import by.epam.tr.service.ServiceException;
import by.epam.tr.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CrewPage implements Command {

    private Logger logger = LogManager.getLogger(getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        ListCreatorService listCreatorService = ServiceFactory.getInstance().getListCreatorService();
        List<String> crews;
        try {

            crews = listCreatorService.createCrewsList();

            request.setAttribute(RequestParameterName.CREW, crews);
            forwardTo(request, response, JSPPageName.CREWS_PAGE);

        } catch (ServiceException e) {
            logger.error("Cannot execute command for crew page", e);
            errorPage(response);
        }
    }
}