# Passo 2 : Configurando acesso ao banco de dados

Nesta etapa, você conseguirá inserir a configuração de acesso ao banco de dados na sua aplicação RESTful com o Spring Boot. 

## Dependências necessárias

Para executar essa configuração, adicionamos a dependência do Spring Data JPA e simularemos um banco em memória com o H2. Nosso arquivo de configuração build.gradle ficou assim:

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

## Configurando um JPA Repository

A definição de modelo do que é um café dentro do nosso sistema passará por algumas mudanças, com a adição de anotações como @Entity e @Table, além de decorações nos atributos como @Id e @Column. 

```
package br.edu.uepb.coffee.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coffees")
public class Coffee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    public Coffee(String name) {
        this.name = name;
    }
}
```

Com isso, o repositório pode ser modificado para uma interface JpaRepository, que deve ser da classe e do tipo do seu identificador. Nosso antigo repositório que simulava uma base de dados com o uso de ArrayList, passou a se chamar CustomCoffeeRepository.

```
package br.edu.uepb.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.uepb.coffee.domain.Coffee;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {}
```

Essa interface contém todos os métodos necessários (vide documentação) para manipulação básica de dados, como *save* para criar ou atualizar registros, de forma dinâmica, ou *find* para buscar registros, inclusive usando os atributos da classe, como por exemplo, *findById*.

Logo, nosso controller ficará dessa forma:

```
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
        return coffeeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Coffee> getCoffeeById(@PathVariable Long id) {
        return coffeeRepository.findById(id);
    }

    @PostMapping
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @PutMapping("/{id}")
    public Coffee updateCoffee(@PathVariable("id") Long id, @RequestBody Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @DeleteMapping("/{id}")
    public void deleteCoffee(@PathVariable Long id) {
        coffeeRepository.delete(coffeeRepository.findById(id).get());
    }
}
```

Você pode perceber o uso dos métodos disponíveis a partir da interface JpaRepository configurada para o tipo Coffee e o tipo do seu identificador, Long.

## Adicionando as credenciais de acesso ao banco de dados

Como falado anteriormente, o banco será simulado com o banco em memória H2. Para tanto, basta inserir as configurações abaixo

```
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

Destacamos a última configuração, na qual é possível acessar o banco de dados via browser, pelo endereço http://localhost:8080/h2-console.

No próximo passo estaremos indicando como incluir as configuração de documentação automatizada com o Swagger.

Para conferir a demonstração completa e o processo de codificação feito na aula, você pode [acessar aqui o vídeo](https://drive.google.com/file/d/175KTpqp1dDhuktMR64UfifRZ0SQJS6TF/view?usp=sharing).
