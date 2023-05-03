package ac.at.fhcampuswien.carinventoryms.service;

import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotFoundException;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import ac.at.fhcampuswien.carinventoryms.repository.CarRepository;

import java.time.LocalDate;
import java.util.List;

public class CarEntityService {
    CarRepository carRepository;


    public List<Car> getFreeCarsBetweenDates(LocalDate from, LocalDate to) throws CarNotAvailableException {

        //TODO RabbitMQ
        List<Long> availableCarIDs = rentalRepository.findAllAvailableCarsBetweenDates(from, to);
        if(availableCarIDs.isEmpty()) availableCarIDs.add(0l);
        List<Car> allAvailableCars = carRepository.findCarsNotInList(availableCarIDs);
        if (allAvailableCars.isEmpty()) {
            throw new CarNotAvailableException("No cars available in this time period");
        }
        return allAvailableCars;
    }

    public Car getCarById(String id) throws CarNotFoundException {
        Car car = carRepository.getCarById(id);
        if(car == null){
            throw new CarNotFoundException("The Car with this ID could not be found!");
        }
        return car;
    }
}
