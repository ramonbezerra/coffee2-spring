# Passo 3 : Configurando uma forma de documentação automatizada

Nesta etapa, você conseguirá inserir a configuração de documentação automatizada com o Swagger na sua aplicação RESTful com o Spring Boot. 

## Dependências necessárias

Para executar essa configuração, adicionamos a dependência do Swagger. Nosso arquivo de configuração build.gradle ficou assim:

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

## Configurando a Documentação

É preciso adicionar um novo pacote, *settings*, e inserir a classe abaixo:

```
package br.edu.uepb.coffee.settings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("br.edu.uepb.coffee"))
                .build().apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title("Coffee Restful API").description("\"Supported by Spring Boot\"")
                .version("1.0.0").license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"").build();
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

Com isso, o projeto Spring reconhecerá um novo componente de configuração, com a anotação @Configuration, e nessa classe temos a ativação das configurações para o Swagger, com a decoração @EnableSwagger2. Configurações essas como a deteção do pacote a ser documentado, no método *productApi*, inserção de metadados no método *metaData* e definição de recursos de visualização, no método *addResourceHandlers*. 

Destacamos a última configuração, na qual é possível acessar o portal de documentação via browser, pelo endereço http://localhost:8080/swagger-ui.html.

## Usando a Documentação no Controller

No CoffeeController é preciso adicionar anotações sobre as rotas, com a anotação @ApiOperation:

```
package br.edu.uepb.coffee.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.edu.uepb.coffee.domain.Coffee;
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
    public List<Coffee> getCoffees() {
        return coffeeRepository.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Busca um café pelo seu identificador")
    public Optional<Coffee> getCoffeeById(@PathVariable Long id) {
        return coffeeRepository.findById(id);
    }

    @PostMapping
    @ApiOperation(value = "Cria um novo café")
    public Coffee createCoffee(@RequestBody Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Atualiza um café a partir do seu identificador")
    public Coffee updateCoffee(@PathVariable("id") Long id, @RequestBody Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Exclui um café a partir do seu identificador")
    public void deleteCoffee(@PathVariable Long id) {
        coffeeRepository.delete(coffeeRepository.findById(id).get());
    }
}
```

Para conferir a demonstração completa e o processo de codificação feito na aula, você pode [acessar aqui o vídeo](https://drive.google.com/file/d/175KTpqp1dDhuktMR64UfifRZ0SQJS6TF/view?usp=sharing).
