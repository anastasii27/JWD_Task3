package by.epam.airport_system.controller.command.front;

import by.epam.airport_system.controller.command.Command;
import by.epam.airport_system.controller.command.front.impl.*;
import java.util.HashMap;
import java.util.Map;

public final class CommandProvider {
    private static final CommandProvider instance =  new CommandProvider();
    private  final Map <CommandName, Command> commands = new HashMap<>();

    private CommandProvider(){
        commands.put(CommandName.SIGN_IN, new SignIn());
        commands.put(CommandName.REGISTER, new Registration());
        commands.put(CommandName.NO_SUCH_COMMAND, new NoSuchCommand());
        commands.put(CommandName.SIGN_OUT, new SignOut());
        commands.put(CommandName.CHANGE_LANGUAGE, new ChangeLanguage());
        commands.put(CommandName.SHOW_REGISTER_PAGE, new RegisterPage());
        commands.put(CommandName.SHOW_SIGN_IN_PAGE, new SignInPage());
        commands.put(CommandName.SHOW_USER_PAGE, new UserPage());
        commands.put(CommandName.SHOW_DEPARTURES_ARRIVALS, new AirportFlightPage());
        commands.put(CommandName.SHOW_MY_FLIGHTS, new MyFlights());
        commands.put(CommandName.SHOW_CREW_PAGE, new CrewPage());
        commands.put(CommandName.SHOW_CREATE_CREW_PAGE, new CreateCrewPage());
        commands.put(CommandName.CREATE_CREW, new CreateCrew());
        commands.put(CommandName.SHOW_DISPATCHER_FLIGHTS, new DispatcherFlights());
        commands.put(CommandName.SHOW_FLIGHT_MANAGEMENT_PAGE, new FlightManagementPage());
        commands.put(CommandName.SHOW_CREATE_FLIGHT_PAGE, new CreateFlightPage());
        commands.put(CommandName.CREATE_FLIGHT, new CreateFlight());
        commands.put(CommandName.FREE_CREWS_FOR_FLIGHT, new ChooseCrewForFlight());
        commands.put(CommandName.EDIT_FLIGHT, new EditFlight());
        commands.put(CommandName.SHOW_FLIGHT_TIMETABLE_PAGE, new FlightTimetablePage());
        commands.put(CommandName.EDIT_USER, new EditUser());
        commands.put(CommandName.SHOW_USER_EDITING_PAGE, new UserEditingPage());
        commands.put(CommandName.CHANGE_LOGIN, new ChangeLogin());
        commands.put(CommandName.CHANGE_PASSWORD, new ChangePassword());
    }

    public Command getCommand(String name){
        CommandName commandName;
        Command command;

        try {
            commandName = CommandName.valueOf(name.toUpperCase());
            command = commands.get(commandName);
        }catch (IllegalArgumentException e){
            command = commands.get(CommandName.NO_SUCH_COMMAND);
        }
        return command;
    }

    public static CommandProvider getInstance(){
        return instance;
    }
}
