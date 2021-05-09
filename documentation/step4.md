# Passo 4: Protegendo o domínio da aplicação

Nesta etapa, você vai aprender a inserir elementos do código que te ajudarão a flexibilizar a comunicação com a interface consumidora da API sem perder as especificações do domínio já configurado na sua aplicação RESTful com o Spring Boot. 

## Dependências necessárias

Para executar essa configuração, adicionamos a dependência do Model Mapper. Nosso arquivo de configuração build.gradle ficou assim:

```
plugins {
	id 'org.springframework.boot' version '2.4.4'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'br.edu.uepb'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'io.springfox:springfox-swagger2:2.9.2'
	implementation 'io.springfox:springfox-swagger-ui:2.9.2'
	implementation 'org.modelmapper:modelmapper:2.3.5'
	runtimeOnly 'com.h2database:h2'
	compileOnly 'org.projectlombok:lombok'
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

test {
	useJUnitPlatform()
}
```

## Adicionando novas classes: os DTOs

É comum existir dados nas interfaces das aplicações que não necessariamente estão alinhados com as definições do domínio da aplicação. O inverso dessa afirmativa também é válido: certos dados do domínio não devem estar expostos na comunicação da interface com a API. Como então proteger os dados do domínio e nos adequar às diferentes situações?

Seguindo com o nosso exemplo da cafeteria, imagine que quiséssemos aplicar um desconto promocional a determinado café, mas isso só ocorre em determinadas épocas ou em situações específicas. Logo, é um dado que, a princípio, pode não interessar a persistência dessa informação no banco de dados. Como receber essa informação e calcular um desconto?

Precisamos inserir um novo tipo de objeto, o DTO (Data Transfer Object), que basicamente é uma classe com atributos simples, que usamos para otimizar a comunicação entre o cliente e o servidor. Podemos ainda definir um DTO como um padrão de arquitetura de objetos, que agrega e encapsula dados para transferência. Na classe DTO, é possível configurar de forma mais livre os atributos e, assim, podemos manipulá-los da forma que quisermos.

Para tanto, é preciso adicionar um novo pacote, *dto*, e inserir a classe abaixo:

`CoffeeDTO.java`
```
package br.edu.uepb.coffee.dto;

import lombok.Data;

@Data
public class CoffeeDTO {
    
    private String name;

    private double price;
}
```

Observe o uso da anotação `@Data` do pacote `lombok`, que inclui as funcionalidades das anotações `@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@ToString` e `@EqualsAndHashCode` numa única linha.

A nova classe `CoffeeDTO` será usada na camada de apresentação, ou seja, no controller.

## Usando o DTO no Controller

No CoffeeController, vamos passar a receber e enviar objetos `CoffeeDTO`. Para tanto, vamos alterar nossas rotas, começando da rota para listar todos os cafés: 

`CoffeeController.java`
```
package br.edu.uepb.coffee.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;
import br.edu.uepb.coffee.repository.CoffeeRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/coffees")
@Api(value = "Coffee")
public class CoffeeController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @GetMapping
    @ApiOperation(value = "Busca uma lista de todos os cafés")
    public List<CoffeeDTO> getCoffees() {
        return coffeeRepository.findAll(); // error
    }

    // other methods ... 
}
```

O problema dessa implementação é que o repositório trabalha apenas com a classe de domínio, `Coffee`. Precisamos mapear os dados dos atributos da classe de domínio para a classe DTO. E aí que entra o uso do pacote Model Mapper.

## Usando o mapeamento automático

Para usar o mapeamento automático, é preciso adicionar uma nova classe no pacote *settings*:

`CoffeeMapperConfig.java`
```
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
```

Os dois métodos anotados como `@Bean` serão componentes genéricos, que fogem aos padrões de classes de configuração `@Configuration`, Repositório de Dados `@Repository` ou Serviços `@Service`. O primeiro é o mapper genérico, necessário para configuração inicial. O segundo, `CoffeeMapper` é uma classe de mapeamento de dados que iremos criar, que, pode fazer apenas o mapeamento automaticamente ou adicionar configurações adicionais, como as que queremos aplicar com o desconto no preço. --todo

A segunda classe será inserida no novo pacote *mapper*:

`CoffeeMapper.java`
```
package br.edu.uepb.coffee.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;

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
}
```

## Corrigindo o controller

Agora que temos a configuração de mapeamento realizada, podemos incluir a nova dependência no nosso controller e retornar o tipo de objeto corretamente na rota de listagem:

`CoffeeController.java`
```
package br.edu.uepb.coffee.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;
import br.edu.uepb.coffee.mapper.CoffeeMapper;
import br.edu.uepb.coffee.repository.CoffeeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/coffees")
@Api(value = "Coffee")
public class CoffeeController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private CoffeeMapper coffeeMapper;

    @GetMapping
    @ApiOperation(value = "Busca uma lista de todos os cafés")
    public List<CoffeeDTO> getCoffees() {
        List<Coffee> coffees = coffeeRepository.findAll();
        return coffees.stream()
                        .map(coffeeMapper::convertToCoffeeDTO)
                        .collect(Collectors.toList());
    }

    // other methods... 
}

```

Para tornar a nossa aplicação mais semântica, vamos adicionar mais DTOs de acordo com o contexto, como também adicionar uma camada de serviços para separação de responsabilidades com relação a possíveis regras de negócio. 

## Adicionando uma camada de serviços (e mais DTOs)

Para aplicar uma regra de negócio relacionada a concessão de descontos nos cafés, vamos criar um DTO específico para esse contexto:

`CoffeeWithDiscountDTO.java`
```
package br.edu.uepb.coffee.dto;

import lombok.Data;

@Data
public class CoffeeWithDiscountDTO {
    
    private String name;
    private double discount;
}
```

Para ele funcionar bem assim como o `CoffeeDTO`, é necessário estabelecer os métodos de mapeamento entre a classe de domínio e a classe de transferência de dados. 

```
package br.edu.uepb.coffee.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.dto.CoffeeDTO;
import br.edu.uepb.coffee.dto.CoffeeWithDiscountDTO;

public class CoffeeMapper {

    @Autowired
    private ModelMapper modelMapper;
    
    // conversion methods between Coffee and CoffeeDTO ... 

    public CoffeeWithDiscountDTO convertToCoffeeWithDiscountDTO(Coffee coffee) {
        CoffeeWithDiscountDTO coffeeDTO = modelMapper.map(coffee, CoffeeWithDiscountDTO.class);

        return coffeeDTO;
    }

    public Coffee convertFromCoffeeWithDiscountDTO(CoffeeWithDiscountDTO coffeeDTO) {
        Coffee coffee = modelMapper.map(coffeeDTO, Coffee.class);
    
        return coffee;
    }
}
```

Agora vamos criar a nossa classe de serviço dentro de um novo pacote chamado *services*:

`CoffeeService.java`
```
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
```

Note a existência de um método além dos métodos de CRUD, o `updateDiscountCoffee`, onde pudemos aplicar uma regra de negócio. Observe também a existência de uma exceção específica criada para a ocasião em que o usuário tentar criar um café cujo nome já exista registrato na base de dados. Esta exceção foi criada no pacote *exceptions*. Além dela, usamos também a exceção `NotFoundException`, para as situações em que um café não é encontrado de acordo com os parâmetros de busca passados como parâmetro.

`ExistingCoffeeSameNameException.java`
```
package br.edu.uepb.coffee.exceptions;

public class ExistingCoffeeSameNameException extends Exception {
    public ExistingCoffeeSameNameException(String message) {
        super(message);
    }
}
```

Bem semântico, não acha? Veja que, com esses recursos, podemos melhorar e muito nosso `CoffeeController`, adicionando um novo endpoint *PATCH*, que se refere a uma atualização parcial dos dados, e o recurso `ResponseEntity` para manipular as respostas HTTP.

`CoffeeController.java`
```
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
```

Note também a manipulação em outras requisições, melhorando o tratamento de erros quando não há dados, usando a classe abaixo:

`GenericResponseErrorDTO.java`
```
package br.edu.uepb.coffee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericResponseErrorDTO {
    private String error;
}
```

Para conferir a demonstração completa e o processo de codificação feito na aula, você pode [acessar aqui o vídeo](https://drive.google.com/file/d/1GgHkKItafcFBuFfjTB_j3tH1_hogLy-g/view?usp=sharing).
