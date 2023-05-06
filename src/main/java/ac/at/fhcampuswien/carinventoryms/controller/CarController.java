package ac.at.fhcampuswien.carinventoryms.controller;


import ac.at.fhcampuswien.carinventoryms.dto.CarListDTO;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CarNotFoundException;
import ac.at.fhcampuswien.carinventoryms.exceptions.CurrencyServiceNotAvailableException;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import ac.at.fhcampuswien.carinventoryms.service.CarRestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api/v1/cars")
@Tag(name = "Cars", description = "Endpoints for managing car inventory")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CarController {

    @Autowired
    private CarRestService carRestService;

    @GetMapping()
    @Operation(
            summary = "Lists all available cars.",
            tags = {"Cars"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class))),
                    @ApiResponse(description = "No cars available in this time period", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Currency Service is not available!", responseCode = "500", content = @Content)
            })
    public ResponseEntity<List<CarListDTO>> listAvailableCars(@RequestParam("currentCurrency") String currentCurrency,
                                                              @RequestParam("chosenCurrency") String chosenCurrency,
                                                              @RequestParam("from") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
                                                              @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to) throws CarNotAvailableException {
        List<CarListDTO> allAvailableCars = carRestService.getAvailableCars(currentCurrency, chosenCurrency, from, to);
        return new ResponseEntity<>(allAvailableCars, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lists specific car.",
            tags = {"Cars"},
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Car.class))),
                    @ApiResponse(description = "The Car with this ID could not be found!", responseCode = "404", content = @Content)
            })
    public ResponseEntity<Car> getCar(@PathVariable("id") String carId) throws CarNotFoundException {
        Car specificCar = carRestService.getSpecificCar(carId);
        return new ResponseEntity<>(specificCar, HttpStatus.OK);
    }

}
