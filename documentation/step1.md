# Passo 1 : Introdução

Nessa primeira etapa, você conseguirá criar sua primeira aplicação RESTful com o Spring Boot. 

## O projeto Spring

O Spring Boot é um projeto da Spring que visa facilitar o processo de configuração e publicação de aplicações para Web, fazendo com que o projeto seja construído o mais rápido possível e sem complicação.

No site do projeto Spring Boot é possível visualizar quais projetos fazem parte das convenções de aplicações possíveis de adicionar ao seu projeto de aplicação Web, como Cloud, Data, Security, entre outros.

## Criando seu primeiro projeto

As IDEs (Ambientes Integrados de Desenvolvimento) como o IntelliJ ou VSCode (através dos pacotes de extensões Java Extension Pack e Spring Boot Tools) dão suporte a criação de projetos baseados em Spring Boot, assim como existe a opção no site [Start Spring](http://start.spring.io/). 

É necessário primeiramente escolher o gerenciador de pacotes, entre Maven e Gradle, a linguagem entre Java (esta, utilizada neste projeto), Kotlin ou Groovy e a versão do Spring estável mais recente (utilizaremos as versões 2.5.x).

Daí por diante, é necessário fornecer os metadados como grupo de pacotes, nome do artefato e do projeto, descrição, a forma de empacotamento entre .jar (utilizada neste projeto) e .war e a versão do Java (a versão 11 é a utilizada neste projeto). 

Nosso arquivo de configuração pom.xml, com as dependências mais básicas como **Spring Boot DevTools**, **Spring Web** e **Lombok**, ficou assim:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.5.13</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>br.edu.uepb</groupId>
	<artifactId>coffee</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>coffee</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>11</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```

## Criando um Controller

Sua aplicação Web, neste projeto, deve ser capaz de receber requisições sobre dados de cafés. Para tanto, é preciso ter uma definição de modelo do que é um café dentro do nosso sistema.

```java
package br.edu.uepb.coffee.domain;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Coffee {

    private String id;
    private String name;

    public Coffee(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}
```

No propósito de acessar quantos cafés estão cadastrados no sistema ou criar um novo, é preciso criar um REST Controller, como no exemplo a seguir, dentro do novo pacote *controller*. Note o uso da anotação @RestController e @RequestMapping. 

```java
package br.edu.uepb.coffee.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class CoffeeController {

    private List<Coffee> coffees = new ArrayList<>();

    @RequestMapping(value = "/coffees", method = RequestMethod.GET)
    public List<Coffee> getCoffees() {
        return coffees;
    }

    @RequestMapping(value = "/coffees", method = RequestMethod.POST)
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        Coffee newCoffee = new Coffee(coffee.getName());
        coffees.add(newCoffee);
        return newCoffee;
    }  
}
```
Veja que estamos silumlando o armazenamento utilizando um ArrayList. Isso será configurado corretamente mais a frente, com a adição do projeto Spring Data.

Antes, veja a segunda opção de anotações, mais simplificada, com o reaproveitamento de rotas colocando a anotação @RequestMapping acima da classe (o que pode permitir o versionamento da API, que é uma boa prática) e o uso de anotações mais simples:

```java
package br.edu.uepb.coffee.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coffees)
public class CoffeeController {

    private List<Coffee> coffees = new ArrayList<>();

    @GetMapping
    public List<Coffee> getCoffees() {
        return coffees;
    }

    @PostMapping
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        Coffee newCoffee = new Coffee(coffee.getName());
        coffees.add(newCoffee);
        return newCoffee;
    }  
}
```

Outros recursos podem ser adicionados ao projeto, como obter um café pelo seu identificador, atualizar ou excluir um café existente.

```java
package br.edu.uepb.coffee.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coffees)
public class CoffeeController {

    private List<Coffee> coffees = new ArrayList<>();

    @GetMapping
    public List<Coffee> getCoffees() {
        return coffees;
    }

    @GetMapping("/{id}")
    public Optional<Coffee> getCoffeeById(@PathVariable String id) {
        for (Coffee coffee : coffees) {
            if (coffee.getId().equals(id))
                return Optional.of(coffee);
        }

        return Optional.empty();
    }

    @PostMapping
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        Coffee newCoffee = new Coffee(coffee.getName());
        coffees.add(newCoffee);
        return newCoffee;
    }

    @PutMapping("/{id}")
    public Coffee updateCoffee(@PathVariable("id") String id, @RequestBody Coffee coffee) {
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

    @DeleteMapping("/{id}")
    public void deleteCoffee(@PathVariable String id) {
        coffees.removeIf(c -> c.getId().equals(id));
    }  
}
```

## Separando responsabilidades

Nosso controller não deve conter a responsabilidade de acesso aos dados, obedecendo aos princípio S do acrônimo SOLID.

Para tanto, vamos criar o pacote *repository* e colocar toda a lógica de acesso aos dados simulados na nova classe, CoffeeRepository.

```java
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
```

Portanto, nosso controller deve ficar mais simples de manter, aplicando outro princípio SOLID, a inversão de dependência, injetando com a anotação @Autowired o CoffeeRepository.

```java
package br.edu.uepb.coffee.controller;

import java.util.List;
import java.util.Optional;

import br.edu.uepb.coffee.domain.Coffee;
import br.edu.uepb.coffee.repository.CoffeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coffees")
public class CoffeeController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @GetMapping
    public List<Coffee> getCoffees() {
        return coffeeRepository.getCoffees();
    }

    @GetMapping("/{id}")
    public Optional<Coffee> getCoffeeById(@PathVariable String id) {
        return coffeeRepository.getCoffeeById(id);
    }

    @PostMapping
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        return coffeeRepository.createCoffee(coffee);
    }

    @PutMapping("/{id}")
    public Coffee updateCoffee(@PathVariable("id") String id, @RequestBody Coffee coffee) {
        return coffeeRepository.updateCoffee(id, coffee);
    }

    @DeleteMapping("/{id}")
    public void deleteCoffee(@PathVariable String id) {
        coffeeRepository.deleteCoffee(id);
    }
}
```

No próximo passo estaremos indicando como incluir a configuração para acesso a banco de dados.
