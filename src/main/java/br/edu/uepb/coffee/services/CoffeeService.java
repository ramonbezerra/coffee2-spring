package br.edu.uepb.coffee.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.exceptions.ExistingCoffeeSameNameException;
import br.edu.uepb.coffee.repository.CoffeeRepository;
import javassist.NotFoundException;

@Service
public class CoffeeService {

    @Autowired
    private CoffeeRepository coffeeRepository;
    
    public Coffee updateDiscountCoffee(Coffee coffee, double discount) throws NotFoundException {
        if (!coffeeRepository.findByName(coffee.getName()).isPresent())
            throw new NotFoundException("Não existe um café com esse nome!");
            
        Coffee coffeeEntity = coffeeRepository.findByName(coffee.getName()).get();
        
        double discountFinal = coffeeEntity.getPrice() * discount;
        coffeeEntity.setPrice(coffeeEntity.getPrice() - discountFinal);
        
        return coffeeRepository.save(coffeeEntity);
    }

    public Coffee createCoffee(Coffee coffee) throws ExistingCoffeeSameNameException {
        if (coffeeRepository.findByName(coffee.getName()).isPresent())
            throw new ExistingCoffeeSameNameException("Já existe um café com esse nome!");
        return coffeeRepository.save(coffee);
    }

    public Coffee updateCoffee(Long id, Coffee coffee) {
        coffee.setId(id);
        return coffeeRepository.save(coffee);
    }

    public List<Coffee> listAllCoffees() {
        return coffeeRepository.findAll();
    }

    public Coffee findById(Long id) throws NotFoundException {
        return coffeeRepository.findById(id).orElseThrow(() -> new NotFoundException("Não existe um café com esse identificador!"));
    }

    public void deleteCoffee(Long id) {
        Coffee coffeeToDelete = coffeeRepository.findById(id).get();
        coffeeRepository.delete(coffeeToDelete);
    }
}
