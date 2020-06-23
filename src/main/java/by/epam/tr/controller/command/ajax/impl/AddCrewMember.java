package by.epam.tr.controller.command.ajax.impl;

import by.epam.tr.bean.User;
import by.epam.tr.controller.command.Command;
import static by.epam.tr.controller.util.RequestParametersExtractor.*;
import by.epam.tr.controller.constant_parameter.RequestParameterName;
import by.epam.tr.service.CrewMemberService;
import by.epam.tr.service.ServiceException;
import by.epam.tr.service.ServiceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AddCrewMember implements Command {

    private Logger logger = LogManager.getLogger(getClass());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        CrewMemberService crewMemberService = ServiceFactory.getInstance().getCrewMemberService();
        String crewName;
        String crewMembers;
        List<User> membersList;
        boolean operationResult;

        crewName = request.getParameter(RequestParameterName.CREW_NAME);
        crewMembers = request.getParameter(RequestParameterName.USER);

        membersList = userFullName(crewMembers);

        System.out.println(membersList);
        try {
            operationResult = crewMemberService.addCrewMember(crewName, membersList);

            response.getWriter().write(String.valueOf(operationResult));

        } catch (ServiceException e) {
            logger.error("Cannot execute ajax command for crew member adding", e);
        } catch (IOException e) {
            logger.error("Cannot write response", e);
        }
    }
}
