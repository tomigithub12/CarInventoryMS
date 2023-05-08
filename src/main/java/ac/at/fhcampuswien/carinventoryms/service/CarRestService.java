package ac.at.fhcampuswien.carinventoryms.service;

import ac.at.fhcampuswien.carinventoryms.config.RabbitMQConfig;
import ac.at.fhcampuswien.carinventoryms.dto.CarListDTO;
import ac.at.fhcampuswien.carinventoryms.dto.ConversionRequestDto;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotFoundException;
import ac.at.fhcampuswien.carinventoryms.mapper.CarMapper;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class CarRestService {

    Logger logger = LoggerFactory.getLogger(CarRestService.class);
    @NonNull
    CarEntityService carEntityService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    MessageConverter messageConverter;

    @Autowired
    CarMapper carMapper;

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public List<CarListDTO> getAvailableCars(String currentCurrency, String chosenCurrency, LocalDate from, LocalDate to) throws CarNotAvailableException {
        ConversionRequestDto conversionRequestDto = new ConversionRequestDto(currentCurrency, chosenCurrency);
        List<CarListDTO> carsToDisplay = new ArrayList<>();

        int bookingDays = (int) DAYS.between(from, to) + 1;
        List<Car> availableCars = carEntityService.getFreeCarsBetweenDates(from, to);
        double exchangeRate = (double) rabbitTemplate.convertSendAndReceive(RabbitMQConfig.CARS_EXCHANGE, RabbitMQConfig.EXCHANGERATE_MESSAGE_QUEUE, conversionRequestDto);

        for (Car c : availableCars) {
            float dailyCostConverted = (float) (c.getDailyCost() * exchangeRate);
            float totalCost = (float) (dailyCostConverted * bookingDays);
            float dailyCostFormatted = formatCosts(dailyCostConverted);
            float totalCostFormatted = formatCosts(totalCost);

            CarListDTO carsWithTotalCost = carMapper.carToDisplayList(c, dailyCostFormatted, totalCostFormatted);
            carsToDisplay.add(carsWithTotalCost);
        }
        return carsToDisplay;
    }

    public Car getSpecificCar(String id) throws CarNotFoundException {
        return carEntityService.getCarById(id);
    }

    private float formatCosts(float costs) {
        String formatDaily = df.format(costs);
        String replacedDaily = formatDaily.replace(",", ".");
        return Float.parseFloat(replacedDaily);
    }
}


