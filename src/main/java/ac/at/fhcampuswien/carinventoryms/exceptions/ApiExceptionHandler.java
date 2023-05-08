package ac.at.fhcampuswien.carinventoryms.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    private final ZonedDateTime timestamp = ZonedDateTime.now(ZoneId.of("Europe/Vienna"));



    @ExceptionHandler(CurrencyServiceNotAvailableException.class)
    public ResponseEntity<?> handleSOAPServiceConnectionError(CurrencyServiceNotAvailableException currencyServiceNotAvailableException) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiException apiException = new ApiException(
                currencyServiceNotAvailableException.getMessage(),
                httpStatus,
                timestamp
        );

        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(CarNotAvailableException.class)
    public ResponseEntity<?> handleCarNotAvailablenError(CarNotAvailableException carNotAvailableException) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                carNotAvailableException.getMessage(),
                httpStatus,
                timestamp
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<?> handleCarNotFoundError(CarNotFoundException carNotFoundException) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        ApiException apiException = new ApiException(
                carNotFoundException.getMessage(),
                httpStatus,
                timestamp
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }


}
