package br.edu.uepb.coffee.settings;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.uepb.coffee.mapper.CoffeeMapper;

@Configuration
public class CoffeeMapperConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CoffeeMapper coffeeMapper() {
        return new CoffeeMapper();
    }

}
