package ac.at.fhcampuswien.carinventoryms;

import ac.at.fhcampuswien.carinventoryms.dto.CarListDTO;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import ac.at.fhcampuswien.carinventoryms.repository.CarRepository;
import ac.at.fhcampuswien.carinventoryms.service.CarRestService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class CarInventoryMsApplication {

    Logger logger = LoggerFactory.getLogger(CarInventoryMsApplication.class);

    @Autowired
    CarRestService carRestService;
    @Autowired
    CarRepository carRepository;
    @PostConstruct
    public void initializeData() throws Exception {
        List<Car> cars = Stream.of(
                new Car("1", 70, "Volkswagen", "Sharan", "140", "2015-01-01", "Diesel", "https://www.autoscout24.at/cms-content-assets/7um8LehWjr8ivfvWpoKWS9-4554fa427501bdcf82d97a5b586f15b1-3jcYw3F2oycED9VRLNLvJ4-e2de141217a08ff758af9afc697554b8-Volkswagen-Golf-2020-1600-04-1100-1100.jpg"),
                new Car("2", 80, "Audi", "Q5", "190", "2020-01-01", "Diesel", "https://m.faz.net/media0/ppmedia/aktuell/295225037/1.7484902/mmobject-still_full/rein-elektrischer-fuenfmeter.jpg"),
                new Car("3", 700, "Ferrari", "Enzo", "860", "2023-01-01", "Petrol", "https://cdn.motor1.com/images/mgl/Znnm7r/s1/ferrari-sp48-unica.jpg"),
                new Car("4", 800, "Lamborghini", "Urus", "650", "2023-01-01", "Petrol", "https://www.lamborghini.com/sites/it-en/files/DAM/lamborghini/facelift_2019/model_gw/urus/2023/model_chooser/urus_performante_m.jpg"),
                new Car("5", 90, "Mercedes Benz", "CLA 180", "122", "2016-05-01", "Petrol", "https://cdn.guterate.net/prod/2022/07/451536_01.jpg")
        ).collect(Collectors.toList());
        carRepository.saveAll(cars);
        logger.warn("Cars Database Inititalization succesful!");

        List<CarListDTO> freeCars = carRestService.getAvailableCars("USD", "EUR", LocalDate.of(2023,01,01), LocalDate.of(2023,07,30));

        logger.warn("Retrieved List of available Cars.");

        if(freeCars.size() > 1){
            logger.warn("Something went wrong. Only one carId expected, but got more!");
        } else {
            logger.warn("Success!");
        }

    }
    public static void main(String[] args) {
        SpringApplication.run(CarInventoryMsApplication.class, args);
    }

}
