package by.epam.tr.service.impl;

import by.epam.tr.bean.Plane;
import by.epam.tr.dao.DAOException;
import by.epam.tr.dao.DAOFactory;
import by.epam.tr.dao.PlaneDao;
import by.epam.tr.service.PlaneService;
import by.epam.tr.service.ServiceException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaneServiceImpl implements PlaneService {
    private PlaneDao dao = DAOFactory.getInstance().getPlaneDao();

    @Override
    public List<Plane> freePlanes(String airportName, LocalDate departureDate, LocalDate destinationDate) throws ServiceException {
        try {
            List<Plane> allPlanes  = dao.allPlanesFromAirport(airportName, destinationDate);
            List<Plane> takenOnFlightPlanes = dao.takenOnFlightPlanes(airportName, departureDate);

            return findFreePlanes(allPlanes, takenOnFlightPlanes);
        } catch (DAOException e) {
            throw new ServiceException("Exception during free planes searching!", e);
        }
    }

    private List<Plane> findFreePlanes(List<Plane> allPlanes, List<Plane> takenOnFlightPlanes ){
        List<Plane> inappropriatePlane = new ArrayList<>();

        for (Plane plane1: allPlanes) {
            for (Plane plane2: takenOnFlightPlanes) {
                if(plane1.getModel().equals(plane2.getModel())){
                   comparePlanesAmount(inappropriatePlane, plane1, plane2);
                }
            }
        }
        allPlanes.removeAll(inappropriatePlane);

        return allPlanes;
    }

    private void comparePlanesAmount(List<Plane> inappropriatePlane, Plane plane1, Plane plane2){
        if(plane1.getAmount()==plane2.getAmount()){
            inappropriatePlane.add(plane1);
        }
        if(plane1.getAmount()> plane2.getAmount()){
            plane1.setAmount(plane1.getAmount()-plane2.getAmount());
        }
    }
}
