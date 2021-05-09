package br.edu.uepb.coffee.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;
import br.edu.uepb.coffee.dto.CoffeeWithDiscountDTO;

public class CoffeeMapper {

    @Autowired
    private ModelMapper modelMapper;
    
    public CoffeeDTO convertToCoffeeDTO(Coffee coffee) {
        CoffeeDTO coffeeDTO = modelMapper.map(coffee, CoffeeDTO.class);

        return coffeeDTO;
    }

    public Coffee convertFromCoffeeDTO(CoffeeDTO coffeeDTO) {
        Coffee coffee = modelMapper.map(coffeeDTO, Coffee.class);
    
        return coffee;
    }

    public CoffeeWithDiscountDTO convertToCoffeeWithDiscountDTO(Coffee coffee) {
        CoffeeWithDiscountDTO coffeeDTO = modelMapper.map(coffee, CoffeeWithDiscountDTO.class);

        return coffeeDTO;
    }

    public Coffee convertFromCoffeeWithDiscountDTO(CoffeeWithDiscountDTO coffeeDTO) {
        Coffee coffee = modelMapper.map(coffeeDTO, Coffee.class);
    
        return coffee;
    }
}
