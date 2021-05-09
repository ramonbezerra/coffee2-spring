package br.edu.uepb.coffee.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;
import br.edu.uepb.coffee.dto.CoffeeWithDiscountDTO;
import br.edu.uepb.coffee.dto.GenericResponseErrorDTO;
import br.edu.uepb.coffee.exceptions.ExistingCoffeeSameNameException;
import br.edu.uepb.coffee.mapper.CoffeeMapper;
import br.edu.uepb.coffee.services.CoffeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;

@RestController
@RequestMapping(value = "/coffees", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
@Api(value = "Coffee")
public class CoffeeController {

    @Autowired
    private CoffeeService coffeeService; 

    @Autowired
    private CoffeeMapper coffeeMapper;

    @GetMapping
    @ApiOperation(value = "Busca uma lista de todos os cafés")
    public List<CoffeeDTO> getCoffees() {
        List<Coffee> coffees = coffeeService.listAllCoffees();
        return coffees.stream()
                        .map(coffeeMapper::convertToCoffeeDTO)
                        .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Busca um café pelo seu identificador")
    public ResponseEntity<?> getCoffeeById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(coffeeMapper.convertToCoffeeDTO(coffeeService.findById(id)), HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(new GenericResponseErrorDTO(e.getMessage()));
        }
    }

    @PostMapping
    @ApiOperation(value = "Cria um novo café")
    public ResponseEntity<?> createCoffee(@RequestBody CoffeeDTO coffeeDTO) {
        try {
            Coffee coffee = coffeeMapper.convertFromCoffeeDTO(coffeeDTO);
            return new ResponseEntity<>(coffeeService.createCoffee(coffee), HttpStatus.CREATED);
        } catch (ExistingCoffeeSameNameException e) {
            return ResponseEntity.badRequest().body(new GenericResponseErrorDTO(e.getMessage()));
        }
    }

    @PatchMapping
    @ApiOperation(value = "Atualiza o valor de um café concedendo desconto")
    public ResponseEntity<?> updateDiscountCoffee(@RequestBody CoffeeWithDiscountDTO coffeeDTO) {
        try {
            Coffee coffee = coffeeMapper.convertFromCoffeeWithDiscountDTO(coffeeDTO);
            Coffee coffeeUpdated = coffeeService.updateDiscountCoffee(coffee, coffeeDTO.getDiscount());
            return new ResponseEntity<>(coffeeMapper.convertToCoffeeDTO(coffeeUpdated), HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(new GenericResponseErrorDTO(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualiza um café a partir do seu identificador")
    public CoffeeDTO updateCoffee(@PathVariable("id") Long id, @RequestBody CoffeeDTO coffeeDTO) {
        Coffee coffee = coffeeMapper.convertFromCoffeeDTO(coffeeDTO);
        return coffeeMapper.convertToCoffeeDTO(coffeeService.updateCoffee(id, coffee));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Exclui um café a partir do seu identificador")
    public void deleteCoffee(@PathVariable Long id) {
        coffeeService.deleteCoffee(id);
    }
}
