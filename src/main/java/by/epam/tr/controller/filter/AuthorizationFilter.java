package by.epam.tr.controller.filter;

import by.epam.tr.bean.User;
import by.epam.tr.controller.command.CommandName;
import by.epam.tr.controller.constant_parameter.JSPPageName;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    private static final String ACTION = "action";
    private static final String USER = "user";
    private static final String ACCESS_TYPE = "authorized";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String action =  req.getParameter(ACTION);
        User user = (User) req.getSession().getAttribute(USER);

        CommandName commandName;
        String accessType;

        if(action!=null) {

            commandName =  CommandName.valueOf(action.toUpperCase());
            accessType = commandName.getAccessType();

            if(user == null && accessType.equals(ACCESS_TYPE)){
                req.getRequestDispatcher("/"+JSPPageName.SIGN_IN_PAGE).forward(req, resp);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}