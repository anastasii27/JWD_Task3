package by.epam.tr.controller.command.impl;

import by.epam.tr.controller.constant_parameter.JSPPageName;
import by.epam.tr.controller.command.Command;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SignOut implements Command{

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);

        session.invalidate();

        try {
            response.sendRedirect(JSPPageName.START_PAGE);
        } catch (IOException e) {
            errorPage( response );
        }


    }
}