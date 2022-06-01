# Passo 2 : Configurando acesso ao banco de dados

Nesta etapa, você conseguirá inserir a configuração de acesso ao banco de dados na sua aplicação RESTful com o Spring Boot. 

## Dependências necessárias

Para executar essa configuração, adicionamos a dependência do Spring Data JPA e simularemos um banco em memória com o H2. Caso você utilize o Maven, o arquivo de configuração pom.xml fica assim:

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
	<artifactId>example</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>example</name>
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
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
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

Caso você utilize o Gradle, o arquivo de configuração build.gradle fica assim:

```gradle
plugins {
	id 'org.springframework.boot' version '2.5.13'
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
	compileOnly 'org.projectlombok:lombok'
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  	runtimeOnly 'com.h2database:h2'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

## Configurando um JPA Repository

A definição de modelo do que é um café dentro do nosso sistema passará por algumas mudanças, com a adição de anotações como @Entity e @Table, além de decorações nos atributos como @Id e @Column. 

```java
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

```java
package br.edu.uepb.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.uepb.coffee.domain.Coffee;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {}
```

Essa interface contém todos os métodos necessários (vide documentação) para manipulação básica de dados, como *save* para criar ou atualizar registros, de forma dinâmica, ou *find* para buscar registros, inclusive usando os atributos da classe, como por exemplo, *findById*.

Logo, nosso controller ficará dessa forma:

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

```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

Destacamos a última configuração, na qual é possível acessar o banco de dados via browser, pelo endereço http://localhost:8080/h2-console.

No próximo passo estaremos indicando como incluir as configuração de documentação automatizada com o Swagger.
