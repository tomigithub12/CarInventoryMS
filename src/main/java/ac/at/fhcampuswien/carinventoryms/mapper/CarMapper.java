package ac.at.fhcampuswien.carinventoryms.mapper;

import ac.at.fhcampuswien.carinventoryms.dto.CarListDTO;
import ac.at.fhcampuswien.carinventoryms.models.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public class CarMapper {

    public CarListDTO carToDisplayList(Car car, float dailyCostConverted, float totalCost){
        return CarListDTO.builder()
                .id(car.getId())
                .dailyCost(dailyCostConverted)
                .brand(car.getBrand())
                .model(car.getModel())
                .hp(car.getHp())
                .buildDate(car.getBuildDate())
                .fuelType(car.getFuelType())
                .imageLink(car.getImageLink())
                .totalCosts(totalCost)
                .build();
    }

}
