package ac.at.fhcampuswien.carinventoryms.rabbitMQ;


import ac.at.fhcampuswien.carinventoryms.config.RabbitMQConfig;
import ac.at.fhcampuswien.carinventoryms.dto.AvailableCarsRequestDto;
import ac.at.fhcampuswien.carinventoryms.dto.CarsResponseDto;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import ac.at.fhcampuswien.carinventoryms.repository.CarRepository;
import ac.at.fhcampuswien.carinventoryms.service.CarEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestListener {

    Logger logger = LoggerFactory.getLogger(RequestListener.class);
    @Autowired
    CarRepository carRepository;

    @Autowired
    CarEntityService carEntityService;

    @RabbitListener(queues = RabbitMQConfig.GET_CARS_MESSAGE_QUEUE)
    public CarsResponseDto onGetCarsRequest(List<String> carIds) {
        logger.warn("Retrieved request from BookingMS");
        CarsResponseDto carsResponseDto = new CarsResponseDto();
        List<Car> cars = carRepository.findCarByIds(carIds);
        carsResponseDto.setCars(cars);
        return carsResponseDto;
    }

    @RabbitListener(queues = RabbitMQConfig.GET_FREE_CARS_MESSAGE_QUEUE)
    public CarsResponseDto onGetFreeCarsBetweenDatesRequest(AvailableCarsRequestDto availableCarsRequestDto) throws CarNotAvailableException {
        logger.warn("Retrieved request from BookingMS");
        CarsResponseDto carsResponseDto = new CarsResponseDto();
        List<Car> cars = carEntityService.getFreeCarsBetweenDates(availableCarsRequestDto);
        carsResponseDto.setCars(cars);
        return carsResponseDto;
    }
}
