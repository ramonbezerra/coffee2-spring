# Passo 7: Executando testes de integração na sua aplicação de forma automatizada

## Testes de Integração

Garantida a qualidade do serviço do café, podemos realizar testes em um nível de camada diferente. Como iremos simular o uso de rotas de forma automatizada, serão testes no nível de integração. Afinal, estaremos simulando que uma outra aplicação faça integração e acesse as rotas da nossa aplicação web com Spring Boot.

Para testar as rotas, faremos uma configuração semelhante aos testes de unidade com a camada de serviços. Estaremos trabalhando com mais dependências anotadas como @Mock, do pacote Mockito.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {
    
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CoffeeService coffeeService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CoffeeMapper coffeeMapper;

    @InjectMocks
    private CoffeeController coffeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coffeeController).build();
    }
}
```

Note que, além de prover os mocks e a classe objeto do teste onde serão injetados, é necessário instanciar dois atributos: o que irá mapear o conteúdo dos arquivos JSON, que é o `ObjectMapper`, e o cliente para chamar a API, o `MockMvc`. Além disso, há outras duas configurações para a classe de teste: decorada com duas anotações, `@ExtendWith` e `@MockitoSettings`.

Para esse nível de testes, iremos introduzir, para fins de organização, o uso da anotação `@Nested`, que irá separar classes internas por tipo de operação, por exemplo.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    private static final String COFFEE_API_URL_PATH = "/coffees";
    private static final Long VALID_COFFEE_ID = 1L;
    private static final Long INVALID_COFFEE_ID = 10L;
    
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CoffeeService coffeeService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CoffeeMapper coffeeMapper;

    @InjectMocks
    private CoffeeController coffeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coffeeController).build();
    }

    @Nested
    class GetMethodsTest {
        // get method tests ...
    }

    @Nested
    class PostMethodsTest {
        // post method tests ...
    }

    @Nested
    class PutMethodsTest {
        // put method tests ...
    }

    @Nested
    class DeleteMethodsTest {
        // delete method tests ...
    }
}
```

## Caso de teste de integração 1: Rotas GET

Para esse primeiro caso de teste, três cenários serão avaliados:

- Se houver um registro de um determinado café no banco, ele será retornado na lista com status OK;
- Se houver um registro de um determinado Id de café no banco, será retornado com status OK;
- Se não houver um registro de um determinado Id de café no banco, será retornado status Bad Request.

Antes de iniciar os testes de fato, é necessário uma configuração para essa seção dos testes. Instanciar dois objetos: um do domínio, utilizando o builder do teste anterior, e um DTO, utilizando um modelo semelhante de builder, contando com os mesmos dados de name e price.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    private static final String COFFEE_API_URL_PATH = "/coffees";
    private static final Long VALID_COFFEE_ID = 1L;
    private static final Long INVALID_COFFEE_ID = 10L;
    
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CoffeeService coffeeService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CoffeeMapper coffeeMapper;

    @InjectMocks
    private CoffeeController coffeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coffeeController).build();
    }

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }
    }

    @Nested
    class PostMethodsTest {
        // post method tests ...
    }

    @Nested
    class PutMethodsTest {
        // put method tests ...
    }

    @Nested
    class DeleteMethodsTest {
        // delete method tests ...
    }
}
```

Para testar o primeiro cenário, faremos a simulação com o método estático `when` do pacote `Mockito` da conversão entre classes de domínio e DTO, além do retorno do serviço com a lista contendo um café.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            when(coffeeMapper.convertToCoffeeDTO(eq(coffee))).thenReturn(coffeeDTO);
            when(coffeeService.listAllCoffees()).thenReturn(List.of(coffee));
        }
    }
}
```

Então, no teste da requisição, utilizando o objeto `mockMvc`, acessamos a rota GET e avaliamos se há um café na lista com o mesmo nome do café que simulamos no serviço.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            when(coffeeMapper.convertToCoffeeDTO(eq(coffee))).thenReturn(coffeeDTO);
            when(coffeeService.listAllCoffees()).thenReturn(List.of(coffee));
            
            mockMvc.perform(get(COFFEE_API_URL_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].name", is(coffee.getName())));
        }
    }
}
```

Para o segundo cenário, simularemos novamente com o método estático `when` do pacote `Mockito` a conversão entre classes de domínio e DTO e do retorno da busca por um id do método do serviço.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
            when(coffeeMapper.convertToCoffeeDTO(eq(coffee))).thenReturn(coffeeDTO);
            when(coffeeService.findById(VALID_COFFEE_ID)).thenReturn(coffee);
        }
    }
}
```

Então, ao testar o acesso a rota, é feito a verificação do status e se o nome do café corresponde ao que simulamos.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
            when(coffeeMapper.convertToCoffeeDTO(eq(coffee))).thenReturn(coffeeDTO);
            when(coffeeService.findById(VALID_COFFEE_ID)).thenReturn(coffee);

            mockMvc.perform(get(COFFEE_API_URL_PATH + "/" + VALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is(coffee.getName())));
        }
    }
}
```

Para o terceiro cenário, avaliaremos se o retorno de uma requisição com id inválido terá o status de Bad Request. Faremos então a simulação de lançamento de exceção a partir do serviço.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithInvalidIdThenBadRequestStatusIsReturned() throws Exception {
            when(coffeeService.findById(INVALID_COFFEE_ID)).thenThrow(CoffeeNotFoundException.class);
        }
    }
}
```

Então, ao performar a requisição, testamos se o status corresponde a Bad Request.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ... 

    @Nested
    class GetMethodsTest {
        private CoffeeDTO coffeeDTO;
        private Coffee coffee;

        @BeforeEach
        void setUp() {
            coffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            coffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenGETIsCalledThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithValidIdThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        void whenGETIsCalledWithInvalidIdThenBadRequestStatusIsReturned() throws Exception {
            when(coffeeService.findById(INVALID_COFFEE_ID)).thenThrow(CoffeeNotFoundException.class);

            mockMvc.perform(get(COFFEE_API_URL_PATH + "/" + INVALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
```

## Caso de teste de integração 2: Rotas POST

Nesse caso de teste, avaliaremos três cenários:

- Se forem passados dados válidos de um café, um café será criado e será retornado status Created;
- Se forem passados dados válidos de um café já criado anteriormente, nada será criado e será retornado status Bad Request;
- Se forem passados dados inválidos de um café, nada será criado e será retornado status Bad Request.

Para testar o primeiro cenário, simularemos utilizando além de objetos dos builders, arquivos JSON para simular a passagem de dados no corpo das requisições. Esses arquivos estarão na pasta `/resources` do projeto de testes.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));
        }
    }
}
```

O arquivo `createCoffeeValidRequest.json` conterá o que estaria no corpo da requisição que estamos testando, como a seguir:

```json
{
    "name": "Expresso",
    "price": 0.5
}
```

Então, simularemos com o método estático `when` do pacote `Mockito` a conversão entre classes de domínio e DTO e do retorno da operação de criação de um café do método do serviço.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));
            
            when(coffeeMapper.convertFromCoffeeDTO(eq(expectedCoffeeDTO))).thenReturn(expectedCoffee);
            when(coffeeService.createCoffee(eq(expectedCoffee))).thenReturn(expectedCoffee);
        }
    }
}
```

Logo, perfmormaremos a requisição POST para verificar se o objeto criado contém o mesmo nome do que foi simulado.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));
            
            when(coffeeMapper.convertFromCoffeeDTO(eq(expectedCoffeeDTO))).thenReturn(expectedCoffee);
            when(coffeeService.createCoffee(eq(expectedCoffee))).thenReturn(expectedCoffee);

            mockMvc.perform(post(COFFEE_API_URL_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(String.valueOf(createCoffeeRequest)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(expectedCoffee.getName())));
        }
    }
}
```

Para o segundo cenário, avaliaremos se, ao tentar criar um café que já existe na base, a requisição retorna o status Bad Request. Começamos lendo o arquivo JSON separado para esse cenário.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            // test method ...
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsTwiceThenBadRequestStatusIsReturned() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));
        }
    }
}
```

Então, simularemos com o método estático `when` do pacote `Mockito` a conversão entre classes de domínio e DTO e do retorno da operação de criação de um café do método do serviço, que, dessa vez, será uma exceção.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            // test method ...
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsTwiceThenBadRequestStatusIsReturned() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));

            when(coffeeMapper.convertFromCoffeeDTO(eq(expectedCoffeeDTO))).thenReturn(expectedCoffee);
            when(coffeeService.createCoffee(eq(expectedCoffee))).thenReturn(expectedCoffee);
            doThrow(ExistingCoffeeSameNameException.class).when(coffeeService).createCoffee(eq(expectedCoffee));
        }
    }
}
```

Logo, faremos a requisição para avaliar se o status retornado nessa operação é Bad Request com a ajuda do objeto `mockMvc`.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            // test method ...
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsTwiceThenBadRequestStatusIsReturned() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeValidRequest.json"),
                            CoffeeDTO.class));

            when(coffeeMapper.convertFromCoffeeDTO(eq(expectedCoffeeDTO))).thenReturn(expectedCoffee);
            when(coffeeService.createCoffee(eq(expectedCoffee))).thenReturn(expectedCoffee);
            doThrow(ExistingCoffeeSameNameException.class).when(coffeeService).createCoffee(eq(expectedCoffee));

            mockMvc.perform(post(COFFEE_API_URL_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.valueOf(createCoffeeRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
```

Para o terceiro cenário, avaliaremos se uma requisição POST que recebe dados inválidos retorna o status de Bad Request. Começaremos lendo novamente o arquivo separado para este teste.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            // test method ...
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsTwiceThenBadRequestStatusIsReturned() throws Exception {
            // test method
        }

        @Test
        void whenPOSTIsCalledWithInvalidFieldsThenBadRequestStatusIsReturned() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));
        }
    }
}
```

O arquivo `createCoffeeInvalidRequest.json` conterá o que estaria no corpo da requisição que estamos testando, como a seguir:

```json
{
    "name": ""
}
```

Então, basta performar a requisição e avaliar o status.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get methods test nested class

    @Nested
    class PostMethodsTest {
        private CoffeeDTO expectedCoffeeDTO;
        private Coffee expectedCoffee;

        @BeforeEach
        void setUp() {
            expectedCoffeeDTO = CoffeeDTOBuilder.builder().build().toCoffeeDTO();
            expectedCoffee = CoffeeBuilder.builder().build().toCoffee();
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsThenShouldObjectCreated() throws Exception {
            // test method ...
        }

        @Test
        void whenPOSTIsCalledWithValidFieldsTwiceThenBadRequestStatusIsReturned() throws Exception {
            // test method
        }

        @Test
        void whenPOSTIsCalledWithInvalidFieldsThenBadRequestStatusIsReturned() throws Exception {
            String createCoffeeRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/createCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));

            mockMvc.perform(post(COFFEE_API_URL_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(String.valueOf(createCoffeeRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
```

## Caso de teste de integração 3: Rotas PUT

Para esse caso de teste, temos três cenários a serem avaliados:

- Se forem passados dados válidos de um café, um café será atualizado e será retornado status OK;
- Se forem passados dados inválidos de um café, nada será atualizado e será retornado status Bad Request;
- Se forem passados dados de ID inválidos de um café, nada será atualizados e será retornado status Not Found.

Para testar o primeiro cenário, temos que alterar os builders para inserir alguma informação nova para atualizar no café que estará na base de dados (de forma mockada), e passar o arquivo com o que seria o corpo da requisição.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeValidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
        }
    }
}
```

Então, com o método `when` do pacote `Mockito`, simulamos novamente a conversão entre DTO e classe de domínio, como também a operação de atualização de um café do método do serviço.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeValidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

            when(coffeeMapper.convertFromCoffeeDTO(eq(coffeeDTO))).thenReturn(coffee);
            when(coffeeService.updateCoffee(eq(VALID_COFFEE_ID), eq(coffee))).thenReturn(coffee);
        }
    }
}
```

Logo, performaremos a requisição PUT e verificamos o status e a informação atualizada:

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeValidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

            when(coffeeMapper.convertFromCoffeeDTO(eq(coffeeDTO))).thenReturn(coffee);
            when(coffeeService.updateCoffee(eq(VALID_COFFEE_ID), eq(coffee))).thenReturn(coffee);

            mockMvc.perform(put(COFFEE_API_URL_PATH + "/" + VALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(String.valueOf(updateCoffeeValidRequest)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }
}
```

O arquivo `updateCoffeeValidRequest.json` conterá o que estaria no corpo da requisição que estamos testando, como a seguir:

```json
{
    "name": "Cappuccino",
    "price": 1.0
}
```

Para o segundo cenário, avaliaremos a requisição com dados inválidos para atualizar. Para isso, temos um novo arquivo JSON para simular essa operação:

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Fields")
        void whenPUTIsCalledWithInvalidDataThenBadRequestStatusIsReturned() throws Exception {
            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));
        }
    }
}
```

Logo, é preciso apenas performar a requisição e verificar se o status é Bad Request.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Fields")
        void whenPUTIsCalledWithInvalidDataThenBadRequestStatusIsReturned() throws Exception {
            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));

            mockMvc.perform(put(COFFEE_API_URL_PATH + "/" + VALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(String.valueOf(updateCoffeeValidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
```

O arquivo `updateCoffeeInvalidRequest.json` conterá o que estaria no corpo da requisição que estamos testando, como a seguir:

```json
{
    "name": ""
}
```

Por fim, para o terceiro cenário, tentaremos atualizar um registro com um id que não está na base.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Fields")
        void whenPUTIsCalledWithInvalidDataThenBadRequestStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Nonexistent Id")
        void whenPUTIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {

            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();
        }
    }
}
```

Logo, com o método estático `when` do pacote `Mockito`, faremos a conversão entre DTO e classe de domínio, e a simulação do lançamento de exceção.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Fields")
        void whenPUTIsCalledWithInvalidDataThenBadRequestStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Nonexistent Id")
        void whenPUTIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {

            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

            when(coffeeMapper.convertFromCoffeeDTO(eq(coffeeDTO))).thenReturn(coffee);
            doThrow(CoffeeNotFoundException.class).when(coffeeService).updateCoffee(INVALID_COFFEE_ID, coffee);
        }
    }
}
```

Então, basta performar a requisição com auxílio do objeto `mockMvc` e verificar o status.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get and post methods test nested class

    @Nested
    class PutMethodsTest {
        @Test
        @DisplayName("Call with Valid Fields")
        void whenPUTIsCalledWithValidDataThenOkStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Fields")
        void whenPUTIsCalledWithInvalidDataThenBadRequestStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Nonexistent Id")
        void whenPUTIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {

            String updateCoffeeValidRequest = objectMapper.writeValueAsString(
                    objectMapper.readValue(
                            new File("src/test/resources/coffee/updateCoffeeInvalidRequest.json"),
                            CoffeeDTO.class));
            CoffeeDTO coffeeDTO = CoffeeDTOBuilder.builder().name("Cappuccino").build().toCoffeeDTO();
            Coffee coffee = CoffeeBuilder.builder().name("Cappuccino").build().toCoffee();

            when(coffeeMapper.convertFromCoffeeDTO(eq(coffeeDTO))).thenReturn(coffee);
            doThrow(CoffeeNotFoundException.class).when(coffeeService).updateCoffee(INVALID_COFFEE_ID, coffee);

            mockMvc.perform(put(COFFEE_API_URL_PATH + "/" + INVALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(String.valueOf(updateCoffeeValidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
```

## Caso de teste de integração 4: Rotas DELETE

Por fim, para as rotas DELETE, temos dois cenários:

- Se for passado um ID de um café válido, este café será deletado e será retornado o status No Content;
- Se for passado um ID de um café inválido, nada será deletado e será retornado o status Not Found.

Para o primeiro cenário, avaliamos uma execução bem-sucedida da operação de excluir um café da base de dados.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get, post and put methods test nested classes

    @Nested
    class DeleteMethodsTest {
        @Test
        void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
            doNothing().when(coffeeService).deleteCoffee(VALID_COFFEE_ID);

            mockMvc.perform(delete(COFFEE_API_URL_PATH + "/" + VALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}
```

Para o segundo cenário, avaliamos se a exceção para um café não encontrado na base de dados é lançada, quando passamos um id inválido na URL da requisição.

```java
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CoffeeControllerTest {

    // attrs and setUp ...
    
    // get, post and put methods test nested classes

    @Nested
    class DeleteMethodsTest {
        @Test
        void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
            // test method ...
        }

        @Test
        @DisplayName("Call with Invalid Id")
        void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
            doThrow(CoffeeNotFoundException.class).when(coffeeService).deleteCoffee(INVALID_COFFEE_ID);

            mockMvc.perform(delete(COFFEE_API_URL_PATH + "/" + INVALID_COFFEE_ID)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }
    }
}
```
