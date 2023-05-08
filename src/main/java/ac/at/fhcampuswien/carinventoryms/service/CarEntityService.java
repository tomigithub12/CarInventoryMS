package ac.at.fhcampuswien.carinventoryms.service;

import ac.at.fhcampuswien.carinventoryms.config.RabbitMQConfig;
import ac.at.fhcampuswien.carinventoryms.dto.AvailableCarsRequestDto;
import ac.at.fhcampuswien.carinventoryms.dto.FreeCarsRequestDto;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotFoundException;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import ac.at.fhcampuswien.carinventoryms.repository.CarRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CarEntityService {

    Logger logger = LoggerFactory.getLogger(CarEntityService.class);
    @Autowired
    CarRepository carRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public List<Car> getFreeCarsBetweenDates(AvailableCarsRequestDto availableCarsRequestDto) throws CarNotAvailableException {

        List<String> availableCarIDsAsJson = availableCarsRequestDto.getBookedCarIds();
        List<String> availableCarIDs = convertJsonListToList(availableCarIDsAsJson);

        if(availableCarIDs.isEmpty()) availableCarIDs.add("0");
        List<Car> allAvailableCars = carRepository.findCarsNotInList(availableCarIDs);
        if (allAvailableCars.isEmpty()) {
            throw new CarNotAvailableException("No cars available in this time period");
        }
        return allAvailableCars;
    }

    public List<Car> getFreeCarsBetweenDates(LocalDate from, LocalDate to) throws CarNotAvailableException {
        FreeCarsRequestDto freeCarsRequestDto = new FreeCarsRequestDto(from, to);

        List<String> availableCarIDsAsJson = (List<String>) rabbitTemplate.convertSendAndReceive(RabbitMQConfig.CARS_EXCHANGE, RabbitMQConfig.BOOKED_CARS_MESSAGE_QUEUE, freeCarsRequestDto);
        List<String> availableCarIDs = convertJsonListToList(availableCarIDsAsJson);

        if(availableCarIDs.isEmpty()) availableCarIDs.add("0");
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

    private List<String> convertJsonListToList(List<String> jsonList) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> carIdList = new ArrayList<>();

        for (String json : jsonList) {
            try {
                // Parse the JSON string
                JsonNode jsonNode = objectMapper.readTree(json);

                // Extract the carId value
                String carId = jsonNode.get("carId").asText();
                carIdList.add(carId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return carIdList;
    }
}
