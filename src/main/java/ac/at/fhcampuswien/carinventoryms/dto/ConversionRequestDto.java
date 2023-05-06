package ac.at.fhcampuswien.carinventoryms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class ConversionRequestDto implements Serializable {

    private String currentCurrency;
    private String chosenCurrency;
}
