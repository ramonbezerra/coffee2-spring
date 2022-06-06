# Passo 6: Executando testes de unidade na sua aplicação de forma automatizada

Nesta etapa, você vai aprender a configurar um projeto a parte na sua aplicação Spring que oferecerá diversas possibilidades de garantir corretude e segurança para a sua implementação, utilizando uma abordagem de desenvolvimento guiado ou orientado por testes (TDD) sobre toda a aplicação RESTful com o Spring Boot, testando no níveis de unidade na camada de serviços.

## Um pouco de testes

Para entender a importância de testar, observe uma frase do famoso cientista holandês Edsger Dijkstra (do problema de grafo de caminho mais curto): "Testes podem ser usados para mostrar a presença de bugs, mas nunca para mostrar sua ausência". Se não há ausência de problemas executando testes, imagina sem eles!

Com testes, queremos identificar falhas, defeitos e erros no software, que são coisas diferentes. Uma falha é um cenário inesperado para o usuário. Já um erro é o que está por trás de uma falha, um caminho no qual faltou atenção do desenvolvedor. E, por fim, um defeito é evidenciado por um erro, algo distante dos requisitos colhidos, uma instrução programada incorretamente. Nesse processo de identificação, é necessário saber que há dois pilares importantes: a Verificação (estamos construindo o sistema corretamente? - Requisitos) e a Validação (estamos construindo o software corretamente? - Regras de Negócio, expectativas do usuário). A verificação tem como propósito averiguar se o software está de acordo com as especificações preestabelecidas, e a validação é o processo de confirmação de que o sistema está apropriado e consistente com os requisitos.

Testar software é o processo de execução de um produto para determinar se ele atingiu suas especificações e funcionou corretamente no ambiente para o qual foi projetado. Logo, testes são importantes para prever o correto processamento dos valores de entrada ou verificar se haverá feedbacks para o usuário em casos de erro, por exemplo.

Você pode até imaginar casos situações com uso de determinados softwares que careciam de testes pela falta de cuidado em situações específicas ou até mesmo a quantidade absurda de erros que são mostrados para o usuário. Desde situações simples como não conseguir fazer um pagamento com um cartão ou um saque no caixa eletrônico, até situações clássicas mostradas na literatura como falhas em foguetes (por exemplo, o foguete Ariane 5, como é o caso mais conhecido) ou máquinas de tomografia que acabaram matando pacientes (um dos casos mais conhecidos é a Therac-25). Mais recentemente, aplicativos de celular como o Caixa Tem durante o Auxílio Emergencial ou Sites de e-commerce durante a Black Friday tem se mostrado como péssimos exemplos de aplicação de testes de software.

Há vários níveis de testes funcionais. Na literatura, os que encontramos são:

- Unidade: testes para pequenas unidades de código, utilizando ferramentas associadas a própria linguagem. No caso do Java, existem o JUnit e o JEST.
- Integração: as unidades de código estarão interligadas com dependências externas, como, por exemplo: conectando a um banco de dados, simulando APIs ou até mesmo chamadas para sistemas externos. Enfim, quase que toda uma funcionalidade.
- Sistema: nesse nível, os testes são feitos sobre uma versão minimamente estável validada por outras pessoas.
- Regressão: é feita uma reavaliação do desenvolvimento, com simulação de novas funcionalidades em comparação as já implementadas. Uma das ferramentas mais conhecidas para testes nesse nível é o Selenium.
- Aceitação: nível mais alto de abstração dos testes, pode ser encarado como um "pente fino".

Por fim, para executar testes de software existem algumas técnicas, conhecidas como Caixa Branca, Caixa Preta e Caixa Cinza.

- Caixa Branca (Teste Estrutural)
  - Tarefas: Validar dados, controles, fluxos, chamadas; garantir a qualidade da Implementação
  - Níveis: Unidade, Integração, Regressão

- Caixa Preta (Teste Funcional)
  - Tarefas: Verificar saídas usando vários tipos de entrada; testar sem conhecer a estrutura interna do software.
  - Níveis: Integração, Sistema, Aceitação

- Caixa Cinza (Mescla os dois anteriores)
  - Tarefas: Analisa lógica e funcionalidade
  - Exemplo: ter acesso a documentação do funcionamento do código
  - Níveis: Engenharia Reversa

Mas, como fazer tudo isso de uma maneira sem consumir tanto tempo? Ou seja, de uma maneira produtiva? Para isso existe uma abordagem de desenvolvimento orientado por testes (TDD, sigla no inglês para *Test-Driven Development*), que consiste em desenvolver testes antes mesmo do código existir. Isso mesmo que você leu! Mas, como isso vai me levar a ter produtividade? Simples, aproveitando-se da automatização desta tarefa.

De maneira mais abstrata, o ciclo do TDD compreende as seguintes etapas:

- Escrever um teste de unidade para uma nova funcionalidade, pensando no resultado desejado;
- Executar o teste e esperar falhar;
- Implementar o código necessário ou o mais simples que possa resolver o problema;
- Executar o teste novamente;
- Refatorar o código até que o teste passe e/ou se houver necessidade.

Para entender melhor esse ciclo, vejamos um exemplo trivial: imagine que desejamos implementar uma nova calculadora. Ela terá pelo menos quatro operações: somar, subtrair, multiplicar e dividir.

Vamos iniciar pela classe de testes. Como estamos de posse de um projeto Spring Boot, considere que já existe uma classe Calculadora (vazia), e queremos testar a operação de soma. Logo, teremos o seguinte:

```java
package br.edu.uepb.coffee;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CoffeeApplicationTests {
 
    @Test
    void testSum() {
        // given - dado: o que o é preciso para testar
        Calculadora c1 = new Calculadora();
        
        // when - quando: o que vamos testar
        int resultado = c1.soma(1,1);
        
        // then - então: resultados a serem comparados
        assertEquals(2, resultado);
    }

}
```

Note que dividimos nosso código de teste em três partes: given (dado uma informação, nesse caso a classe Calculadora), when (quando eu executar determinada operação, a que eu desejo testar) e then (então terei o seguinte resultado para comparar) para facilitar a nossa compreensão do teste de unidade automatizado. O método de testes também possui uma diferença dos demais: a anotação `@Test`.

Para esse código, funcionar, é preciso existir um método `soma(int i, int j)` na classe Calculadora. Escrevendo o teste, você consegue pensar antes da estrutura existir quais parâmetros (e o tipo de cada um) esse método deve receber e qual será o tipo do resultado.

Como a implementação será genérica, o resultado desse testes irá falhar (barra vermelha - haverá trabalho a fazer!), pois o Java não irá advinhar que é preciso fazer a soma dos dois parâmetros e retornar essa soma. Aí que entram os dois últimos passos do ciclo: dado que você implementou o teste e viu ele falhar, implemente a solução mais simples (retornar i + j) e rode o teste novamente.

Se for necessário, seja por erro ou por mudança na especificação, após essas etapas, refatore seu teste e/ou código até estarem em conformidade e finalmente passarem (barra verde!).

Poderíamos ir além desse código, testando uma situação que não desse certo, e ver se esse erro seria detectável. Por exemplo, para a mesma operação, poderíamos verificar que o resultado de 1 + 1 não é 3.

```java
package br.edu.uepb.coffee;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CoffeeApplicationTests {
 
    @Test
    void testSum() {
        // given - dado: o que o é preciso para testar
        Calculadora c1 = new Calculadora();
        
        // when - quando: o que vamos testar
        int resultado = c1.soma(1,1);
        
        // then - então: resultados a serem comparados
        assertNotEquals(3, resultado);
    }

}
```

Repita esse código com as demais operações da calculadora e treine sua nova mentalidade de testar primeiro antes de prosseguir para os próximos tópicos, onde testaremos as regras de negócio e as rotas da nossa aplicação.

## Conhecendo a estrutura dos Testes de Unidade

Numa aplicação web com Spring Boot, testes de unidade podem ser aplicados também às menores unidades de código em cada camada. Nesse caso, como estamos utilizando JPA Repositories - amplamente utilizados e testados, a primeira camada que devemos focar é a de negócio, ou seja, nossos services.

Note antes que, no projeto Spring Boot, já vem provisionado toda uma estrutura para serem executados os testes automatizados. Veja inicialmente como é o formato da classe como a seguir:

```java
package br.edu.uepb.coffee;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoffeeApplicationTests {

    @Test
    void contextLoads() {
    }

}
```

Há a anotação `@Test` para o método de teste e a anotação `@SpringBootTest` para diferenciar esta aplicação da principal. Ou seja, teremos dois perfis ou ambientes diferentes.

Para iniciar os testes de unidade, porém, vamos utilizar uma classe separada, como a seguir:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    @Mock
    private CoffeeRepository coffeeRepository;

    @InjectMocks
    private CoffeeService coffeeService;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }
}
```

Note o uso de algumas anotações e atributos: `@BeforeEach` e `@AfterEach` para os métodos que precisam ser executados antes e depois de cada teste, respectivamente, e a injeção de mocks (dublês) do repositório e do serviço onde há a injeção de dependência do repositório.

### Caso de teste de unidade 1: salvando um café

Para iniciar a criação dos nossos métodos de teste, vamos pensar no caso mais simples, de criar um novo café utilizando o `CoffeeService`. Seguindo a mesma abordagem do given, when e then, começamos pelo que teremos de informação para testar. Nesse caso, será um objeto `Coffee` montado num builder a parte do pacote de teste, como no exemplo a seguir:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();
    }
}
```

Note que o método segue uma convenção que facilita a identificação do objetivo do teste, mas isso é flexível, podendo ser documentado de outra maneira.

Continuando com a construção do teste, vamos definir qual operação será testada, que, nesse caso, será o método `createCoffee` do nosso service. Para efeito de comparação e de uso dos mocks, vamos simular a inserção via repositório desse objeto com o método estático `when` do pacote `Mockito`.

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.save(expectedSavedCoffee))
            .thenReturn(expectedSavedCoffee);
    }
}
```

Nessa operação, é esperado que seja salvo na memória o objeto `expectedSavedCoffee`, que é esperado, e ele seja o retornado pelo repositório para comparar nas operações da parte then.

Então, testaremos diretamente a operação e faremos as verificações por meio das assertivas dos métodos do `Hamcrest`, que são o `assertThat`, `is` e `equalTo`:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.save(expectedSavedCoffee))
            .thenReturn(expectedSavedCoffee);

        //then
        Coffee createdCoffee = coffeeService.createCoffee(expectedSavedCoffee);

        assertThat(createdCoffee.getId(), is(equalTo(expectedSavedCoffee.getId())));
        assertThat(createdCoffee.getName(), is(equalTo(expectedSavedCoffee.getName())));
    }
}
```

Comparamos os atributos id e name, certificando que o objeto é exatamente o que mandamos salvar via service.

É possível (e altamente recomendável) não somente testar o caminho feliz, mas também caminhos de exceção. Vejamos o próximo exemplo, onde iremos testar que não é possível salvar dois cafés com o mesmo nome, e que isso lance uma determinada exceção.

Utilizando a mesma abordagem given, when e then, daremos as informações necessárias para o teste ser executado: nesse caso, dois objetos duplicados, como a seguir.

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // test method ...
    }

    @Test
    void whenAlreadyRegisteredCoffeeInformedThenAnExceptionShouldBeThrown() {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee duplicatedCoffee = expectedSavedCoffee;
    }
}
```

Daí por diante, agora na fase when, iremos simular que, quando for buscado um objeto na base de dados via repositório, ele retorne um objeto idêntico ao duplicado.

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // test method ...
    }

    @Test
    void whenAlreadyRegisteredCoffeeInformedThenAnExceptionShouldBeThrown() {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee duplicatedCoffee = expectedSavedCoffee;

        // when
        when(coffeeRepository.findByName(expectedSavedCoffee.getName())).thenReturn(Optional.of(duplicatedCoffee));
    }
}
```

Sendo assim, na última fase, then, por meio do método estático `assertThrows` do pacote `Hamcrest`, é possível comparar se houve alguma exceção lançada ao chamar o método `createCoffee` passando o objeto duplicado:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    @Test
    void whenNewValidCoffeeInformedThenItShouldBeCreated() throws ExistingCoffeeSameNameException {
        // test method ...
    }

    @Test
    void whenAlreadyRegisteredCoffeeInformedThenAnExceptionShouldBeThrown() {
        // given 
        Coffee expectedSavedCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee duplicatedCoffee = expectedSavedCoffee;

        // when
        when(coffeeRepository.findByName(expectedSavedCoffee.getName())).thenReturn(Optional.of(duplicatedCoffee));

        // then
        assertThrows(ExistingCoffeeSameNameException.class, 
            () -> coffeeService.createCoffee(duplicatedCoffee));
    }
}
```

### Caso de teste de unidade 2: recuperando um café

Cada objeto café inserido na base de dados pode ser consultado nos métodos `listAllCoffees` e `findById`. Então, escreveremos um teste para cada um.

Podemos testar alguns cenários, como:

- se eu não inserir nenhum café e pedir para listar todos, o retorno deve ser uma lista vazia;
- se eu inserir x cafés e pedir para listar todos, o retorno deve ser uma lista de tamanho x;
- se eu pedir um café que foi inserido na base de dados, deve ser retornado exatamente aquele café.
- se eu pedir um café que não foi inserido na base de dados, deve ser retornada uma exceção;

Vamos testar o primeiro cenário. Como não queremos retornar nenhum registro, nenhuma informação será fornecida na etapa given. Somente na parte when faremos a simulação de que não nenhum café no banco de dados.

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        //when
        when(coffeeRepository.findAll()).thenReturn(Collections.emptyList());
    }
}
```

Por fim, basta verificar, contando com a ajuda de outro método do Hamcrest, `empty`, para aferir se realmente a base é vazia sem adicionar ninguém.

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        //when
        when(coffeeRepository.findAll()).thenReturn(Collections.emptyList());

        //then
        List<Coffee> foundListCoffeesDTO = coffeeService.listAllCoffees();

        assertThat(foundListCoffeesDTO, is(empty()));
    }
}
```

Para garantir que o serviço retorna todos os cafés inseridos, o teste deverá ter, na fase given, no mínimo, dois cafés, para garantir a ausência de viés no teste, como no código a seguir:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // given
        Coffee coffee1 = CoffeeBuilder.builder().build().toCoffee();
        Coffee coffee2 = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
        List<Coffee> expectedList = List.of(coffee1, coffee2);
    }
}
```

A operação de simulação, na fase when, deve ser para retornar a lista com os dois cafés:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // given
        Coffee coffee1 = CoffeeBuilder.builder().build().toCoffee();
        Coffee coffee2 = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
        List<Coffee> expectedList = List.of(coffee1, coffee2);

        // when
        when(coffeeRepository.findAll()).thenReturn(expectedList);
    }
}
```

Então, na fase de verificação, then, você pode fazer algumas comparações: se a lista não é vazia e se é do mesmo tamanho da lista esperada:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // given
        Coffee coffee1 = CoffeeBuilder.builder().build().toCoffee();
        Coffee coffee2 = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
        List<Coffee> expectedList = List.of(coffee1, coffee2);

        // when
        when(coffeeRepository.findAll()).thenReturn(expectedList);

        // then
        List<Coffee> foundListCoffees = coffeeService.listAllCoffees();

        assertThat(foundListCoffees, not(empty()));
        assertThat(foundListCoffees.size(), is(equalTo(expectedList.size())));
    }
}
```

Caso a base de dados tenha algum ou alguns registros, é obrigatório retorná-los através do método `listAllCoffees`. Então, vamos ao próximo cenário. Dado um café, simularemos seu retorno ao consultar o repositório por meio do método `findByName`. Essas são as fases given e when:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // test method ...
    }

    @Test
    void whenRegisteredCoffeeNameIsGivenThenReturnACoffee() throws CoffeeNotFoundException {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findByName(expectedFoundCoffee.getName())).thenReturn(Optional.of(expectedFoundCoffee));
    }
}
```

Por fim, na fase then, basta comparar o café buscado com o café esperado:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // test method ...
    }

    @Test
    void whenRegisteredCoffeeNameIsGivenThenReturnACoffee() throws CoffeeNotFoundException {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findByName(expectedFoundCoffee.getName())).thenReturn(Optional.of(expectedFoundCoffee));

        // then
        Coffee foundCoffee = coffeeService.findByName(expectedFoundCoffee.getName());

        assertThat(foundCoffee, is(equalTo(expectedFoundCoffee)));
    }
}
```

O último cenário desse caso de teste é averiguar se é lançada uma exceção quando um café não cadastrado na base for consultado. De forma semelhante aos anteriores, nós passamos um café, que, nesse caso, seria o esperado, na fase given. Na fase when, dessa vez, retornaremos uma base vazia (você também pode retornar uma base com outro café, diferente do primeiro):

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // test method ...
    }

    @Test
    void whenRegisteredCoffeeNameIsGivenThenReturnACoffee() throws CoffeeNotFoundException {
        // test method ... 
    }

    @Test
    void whenNotRegisteredCoffeeNameIsGivenThenThrowAnException() {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findByName(expectedFoundCoffee.getName())).thenReturn(Optional.empty());
    }
}
```

Daí é só conferir se houve exceção lançada:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    @Test
    void whenListCoffeeIsCalledThenReturnAnEmptyListOfCoffees() { 
        // test method ...
    }

    @Test
    void whenRegisteredMoreCoffeesListIsGivenThenReturnAListOfCoffees() {
        // test method ...
    }

    @Test
    void whenRegisteredCoffeeNameIsGivenThenReturnACoffee() throws CoffeeNotFoundException {
        // test method ... 
    }

    @Test
    void whenNotRegisteredCoffeeNameIsGivenThenThrowAnException() {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findByName(expectedFoundCoffee.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(CoffeeNotFoundException.class, () -> coffeeService.findByName(expectedFoundCoffee.getName()));
    }
}
```

### Caso de teste de unidade 3: atualizando um café

Seguindo o raciocínio de testes, agora para o método `updateCoffee`, temos alguns cenários possíveis:

- se dados de um café já existente na base de dados forem fornecidos, o café deve ser atualizado e, quando consultado, ter exatamente os novos dados;
- se os dados de um café não existente na base de dados forem fornecidos, uma exceção deve ser retornada.

Considerando o primeiro cenário, nosso método começa com dois cafés: um que seria o registro atual, e o outro, a atualização:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    @Test
    void whenValidCoffeeIsGivenThenReturnAnUpdatedCoffee() throws CoffeeNotFoundException, ExistingCoffeeSameNameException {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee expectedUpdatedCoffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
    }
}
```

Em seguida, é preciso simular que o repositório contém o primeiro registro e que, ao atualizar com o método `save`, ele retorna o segundo:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    @Test
    void whenValidCoffeeIsGivenThenReturnAnUpdatedCoffee() throws CoffeeNotFoundException, ExistingCoffeeSameNameException {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee expectedUpdatedCoffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

        // when
        when(coffeeRepository.findById(expectedFoundCoffee.getId())).thenReturn(Optional.of(expectedFoundCoffee));
        when(coffeeRepository.save(expectedUpdatedCoffee)).thenReturn(expectedUpdatedCoffee);
    }
}
```

Por fim, basta fazer a verificação do método `updateCoffee`:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    @Test
    void whenValidCoffeeIsGivenThenReturnAnUpdatedCoffee() throws CoffeeNotFoundException, ExistingCoffeeSameNameException {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();
        Coffee expectedUpdatedCoffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

        // when
        when(coffeeRepository.findById(expectedFoundCoffee.getId())).thenReturn(Optional.of(expectedFoundCoffee));
        when(coffeeRepository.save(expectedUpdatedCoffee)).thenReturn(expectedUpdatedCoffee);

        // then
        Coffee updatedCoffee = coffeeService.updateCoffee(expectedFoundCoffee.getId(), expectedUpdatedCoffee);

        assertThat(updatedCoffee, is(equalTo(expectedUpdatedCoffee)));
    }
}
```

Para testar o segundo cenário, nas duas primeiras fases, given e when, pode-se oferecer um café, que seria o esperado, mas não irá retornar. Simularemos uma base vazia:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    @Test
    void whenValidCoffeeIsGivenThenReturnAnUpdatedCoffee() throws CoffeeNotFoundException, ExistingCoffeeSameNameException {
        // test method ...
    }

    @Test
    void whenInvalidCoffeeIsGivenThenThrowAnException() {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findById(expectedFoundCoffee.getId())).thenReturn(Optional.empty());
    }
}
```

Por fim, basta fazer a verificação na fase then se houve exceção lançada:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    @Test
    void whenValidCoffeeIsGivenThenReturnAnUpdatedCoffee() throws CoffeeNotFoundException, ExistingCoffeeSameNameException {
        // test method ...
    }

    @Test
    void whenInvalidCoffeeIsGivenThenThrowAnException() {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findById(expectedFoundCoffee.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(CoffeeNotFoundException.class, () -> coffeeService.updateCoffee(expectedFoundCoffee.getId(), expectedFoundCoffee));
    }
}
```

### Caso de teste de unidade 4: deletando um café

Por fim, para a operação de excluir um registro de um café da base de dados, alguns cenários podem ser testados, tais como:

- se os dados de um café não existente na base de dados forem fornecidos, uma exceção deve ser retornada.
- se dados de um café já existente na base de dados forem fornecidos, o café deve ser excluído e, quando consultado, não existir mais;

Para o primeiro cenário, o código é muito semelhante ao que foi testado quando um café não existente na base era consultado pelo nome. Dessa vez será pelo Id:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    // update tests ... 

    @Test
    void whenNotRegisteredCoffeeIdIsGivenThenThrowAnException() {
        // given
        Coffee expectedFoundCoffee = CoffeeBuilder.builder().build().toCoffee();

        // when
        when(coffeeRepository.findById(expectedFoundCoffee.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(CoffeeNotFoundException.class, () -> coffeeService.deleteCoffee(expectedFoundCoffee.getId()));
    }
}
```

E para o cenário de excluir um café existente na base, nós encontraremos um código bem semelhante nas primeiras duas fases:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    // update tests ... 

    @Test
    void whenNotRegisteredCoffeeIdIsGivenThenThrowAnException() {
        // test method ...
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenACoffeeShouldBeDeleted() throws CoffeeNotFoundException {
        //given
        Coffee expectedDeletedCoffee = CoffeeBuilder.builder().build().toCoffee();

        //when
        when(coffeeRepository.findById(expectedDeletedCoffee.getId())).thenReturn(Optional.of(expectedDeletedCoffee));
        doNothing().when(coffeeRepository).delete(expectedDeletedCoffee);
    }
}
```

Veja o uso de um novo método estático do Mockito, o `doNothing`, para evitar erros ao tentar excluir algo de uma base que não existe. É só simulação.

Para fazer a verificação, neste caso, basta contar o número de vezes que o repositório foi acessado em cada operação:

```java
package com.example.coffee.service;

// imports ... 

public class CoffeeServiceTest {

    // mocks ...

    // setup ...

    // create tests ...

    // read tests ...

    // update tests ... 

    @Test
    void whenNotRegisteredCoffeeIdIsGivenThenThrowAnException() {
        // test method ...
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenACoffeeShouldBeDeleted() throws CoffeeNotFoundException {
        //given
        Coffee expectedDeletedCoffee = CoffeeBuilder.builder().build().toCoffee();

        //when
        when(coffeeRepository.findById(expectedDeletedCoffee.getId())).thenReturn(Optional.of(expectedDeletedCoffee));
        doNothing().when(coffeeRepository).delete(expectedDeletedCoffee);

        //then
        coffeeService.deleteCoffee(expectedDeletedCoffee.getId());

        verify(coffeeRepository, times(1)).findById(expectedDeletedCoffee.getId());
        verify(coffeeRepository, times(1)).delete(expectedDeletedCoffee);
    }
}
```

No próximo passo faremos testes a nível de integração!
