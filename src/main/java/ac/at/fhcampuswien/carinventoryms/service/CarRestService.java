package ac.at.fhcampuswien.carinventoryms.service;

import ac.at.fhcampuswien.carinventoryms.dto.CarListDTO;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotFoundException;
import ac.at.fhcampuswien.carinventoryms.mapper.CarMapper;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class CarRestService {
    @NonNull
    CarEntityService carEntityService;

    @Autowired
    CarMapper carMapper;

    private static final DecimalFormat df = new DecimalFormat("#.##");

    public List<CarListDTO> getAvailableCars(String currentCurrency, String chosenCurrency, LocalDate from, LocalDate to) throws CarNotAvailableException, CurrencyServiceNotAvailableException {
        List<CarListDTO> carsToDisplay = new ArrayList<>();
        int bookingDays = (int) DAYS.between(from, to) + 1;
        List<Car> availableCars = carEntityService.getFreeCarsBetweenDates(from, to);
        double exchangeRate = getExchangeRate(currentCurrency, chosenCurrency);

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

    //TODO RabbitMQ
    public Double getExchangeRate(String currentCurrency, String chosenCurrency) throws CurrencyServiceNotAvailableException {
        GetConvertedValue getConvertedValue = new GetConvertedValue(1f, currentCurrency, chosenCurrency);
        return currencySOAPService.getConvertedValue(getConvertedValue);
    }
}


