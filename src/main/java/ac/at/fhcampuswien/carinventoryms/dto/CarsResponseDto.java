package ac.at.fhcampuswien.carinventoryms.dto;


import ac.at.fhcampuswien.carinventoryms.models.Car;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarsResponseDto implements Serializable {

    private List<Car> cars;

}
