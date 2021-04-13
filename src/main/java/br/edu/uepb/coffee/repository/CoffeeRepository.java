package br.edu.uepb.coffee.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.edu.uepb.coffee.domain.Coffee;

@Repository
public class CoffeeRepository {

    private List<Coffee> coffees = new ArrayList<>();
    
    public List<Coffee> getCoffees() {
        return coffees;
    }

    public Optional<Coffee> getCoffeeById(String id) {
        for (Coffee coffee : coffees) {
            if (coffee.getId().equals(id))
                return Optional.of(coffee);
        }

        return Optional.empty();
    }

    public Coffee updateCoffee(String id, Coffee coffee) {
        int coffeeIndex = -1;
        Coffee updatedCoffee = new Coffee(coffee.getName());

        for (Coffee c : coffees) {
            if (c.getId().equals(id)) {
                coffeeIndex = coffees.indexOf(c);
                coffees.set(coffeeIndex, updatedCoffee);
            }
        }

        return (coffeeIndex == -1) ? createCoffee(coffee) : updatedCoffee;
    }

    public Coffee createCoffee(Coffee coffee) {
        Coffee newCoffee = new Coffee(coffee.getName());
        coffees.add(newCoffee);
        return newCoffee;
    }

    public void deleteCoffee(String id) {
        coffees.removeIf(c -> c.getId().equals(id));
    }
}
