# Passo 5: Adicionando segurança à aplicação

Nesta etapa, você vai aprender a configurar a parte de segurança da aplicação RESTful com o Spring Security, inserindo elementos no código que oferecem, de forma simples e flexível, recursos que te ajudarão a definir e proteger a comunicação entre as rotas e a interface consumidora da API de duas formas: com o uso de credenciais simples (usuário e senha) e Tokens JWT. 

## Dependências necessárias

Para executar essa configuração, adicionamos a dependência do Spring Security. Nosso arquivo de configuração pom.xml ficou assim:

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
		    <groupId>io.springfox</groupId>
		    <artifactId>springfox-swagger2</artifactId>
		    <version>2.9.2</version>
		</dependency>
		<dependency>
		    <groupId>io.springfox</groupId>
		    <artifactId>springfox-swagger-ui</artifactId>
		    <version>2.9.2</version>
		</dependency>
		<dependency>
			<groupId>org.modelmapper</groupId>
			<artifactId>modelmapper</artifactId>
			<version>2.4.5</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
    		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.5</version>
			<scope>runtime</scope>
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

## Entendendo a estrutura do Spring Security

Para gerenciar a segurança de aplicações, o projeto Spring Security atua da seguinte maneira:

![Atores envolvidos no processo de autenticação do Spring Security.](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH02_F02_Spilca.png)

- O AuthenticationFilter delega a solicitação de autenticação ao gerenciador de autenticação e, com base na resposta, configura o contexto de segurança.
- O AuthenticationManager usa o provedor de autenticação para processar a autenticação.
- O AuthenticationProvider implementa a lógica de autenticação.
- O UserDetailsService do usuário implementa a responsabilidade de gerenciamento de usuários, que o provedor de autenticação usa na lógica de autenticação.
- O PasswordEncoder implementa o gerenciamento de senha, que o provedor de autenticação usa na lógica de autenticação.
- O SecurityContext mantém os dados de autenticação após o processo de autenticação.

Quando a dependência do Spring Security é adicionada, nenhuma rota, por default, será mais acessível sem uma autenticação básica. Ao executar a aplicação, note que há algo semelhante à seguinte mensagem:

```prompt
Using generated security password: 93a01cf0-794b-4b98-86ef-54860f36f7f3
```

Isso significa que, para acessar qualquer rota, é necessário incluir as seguintes credenciais: `user:93a01cf0-794b-4b98-86ef-54860f36f7f3`. Porém, isso já representa um risco, que, por mais que essa senha seja aleatória, pode ser facilmente capturada pelo log da aplicação, que é público. 

## Customizando a autenticação básica com usuário fixo

Para configurar uma primeira forma de acesso com usuário e senha, é necessário implementar uma classe de configuração. Para tanto, no novo pacote `settings`, incluiremos a classe de configuração abaixo:

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig {

	@Bean
    public UserDetailsService userDetailsService() {
		var userDetailsService = new InMemoryUserDetailsManager();

		var user = User.withUsername("joaozinho")
		.password("123456")
		.authorities("user")
		.build();

		userDetailsService.createUser(user);

		return userDetailsService;
    }
}
```

Essa classe, por si só, já nos mostra um dos atores envolvidos no esquema de funcionamento do Spring Security, porém, não é o suficiente. Para que a aplicação reconheça corretamente a senha fornecida, é necessário informar qual o esquema de criptografia. Iremos adotar um esquema vazio (sem criptografia), por enquanto, criando outra classe para isso, no mesmo pacote:

`PasswordEncoderConfig.java`
```java
@Configuration
public class PasswordEncoderConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

Agora é possível acessar a aplicação com as credenciais `joaozinho:123456`. 

Na classe `SecurityConfig.java` é possível editar a configuração de autorização, sobrescrevendo mais um dos atores envolvidos no esquema de funcionamento do Spring Security, herdando da classe `WebSecurityConfigurerAdapter`, ficando assim:

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
    public UserDetailsService userDetailsService() {
		// ... implementation
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	 	http.httpBasic();
		// acesso ao Banco de Dados em memória (H2) - origem de endereço diferente (CORS)
        http.cors().and().csrf().disable();
        http.headers().frameOptions().sameOrigin(); 
		
		http.authorizeRequests().anyRequest().authenticated(); // ou .permitAll() para anular a necessidade de autenticação
	}
}
```

Nesse caso, a aplicação está requisitando uma autenticação básica com usuário e senha para todas as rotas.

## Utilizando criptografia

Para implementar um mecanismo de criptografia, é necessário substituir o bean que criamos na classe `PasswordEncoderConfig` como a seguir:

`PasswordEncoderConfig.java`
```java
@Configuration
public class PasswordEncoderConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

Dessa forma, a configuração de segurança poderia ficar assim:

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Bean
    public UserDetailsService userDetailsService() {
		var userDetailsService = new InMemoryUserDetailsManager();

		var user = User.withUsername("joaozinho")
		.password(bCryptPasswordEncoder.encode("123456"))
		.authorities("user")
		.build();

		userDetailsService.createUser(user);

		return userDetailsService;
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// ... implementation
	}
}
```

Essa classe também poderia fazer uso de um outro método de configuração, ao invés do bean, como a seguir:

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        var userDetailsService = new InMemoryUserDetailsManager();

        var user = User.withUsername("joaozinho")
        .password(bCryptPasswordEncoder.encode("0000"))
        .authorities("user")
        .build();

        userDetailsService.createUser(user);

        auth.userDetailsService(userDetailsService);
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// ... implementation
	}
}
```

Essa forma irá facilitar um caminho para simplificar as responsabilidades das classes envolvidas nesse gerenciamento customizado do Spring Security, que é a criação de um AuthenticationProvider, em um pacote de componentes, como a seguir:

`CustomAuthenticationProvider.java`
```java
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        if ("joaozinho".equals(username) && "12345".equals(password)) {
            return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList());
        } else {
            throw new AuthenticationCredentialsNotFoundException("Error in authentication!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

Na classe anterior, tiramos a responsabilidade de armazenamento das informações de quais usuários são permitidos (nesse caso, apenas joaozinho), flexibilizando o uso na classe de configuração:

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    private CustomAuthenticationProvider authenticationProvider;

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// ... implementation
	}
}
```

Antes de prosseguir para o próximo tópico, configure o segundo método para permitir que algumas rotas na aplicação possam ser acessadas sem a necessidade de autenticação: 

`SecurityConfig.java`
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
    private CustomAuthenticationProvider authenticationProvider;
	
	private static final String[] AUTH_WHITELIST = {
        "/v2/api-docs",
        "/signup",
        "/h2-console/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**"
    };

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        // acesso ao Banco de Dados em memória (H2)
        http.cors().and().csrf().disable();
        http.headers().frameOptions().sameOrigin(); 
        http.authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .mvcMatchers("/coffees/**").authenticated();
    }
}
```

## Customizando a autenticação básica com acesso ao banco de dados

Nosso usuário joaozinho, até aqui, é mockado e não persiste em algum banco de dados. Para tanto, teremos que cadastrar usuários, gerenciando o acesso por meio de username e senha, que serão as credenciais para geração de tokens e posterior acesso aos recursos da API. Para tanto, é preciso adicionar uma nova entidade do domínio, como na classe a seguir. *Lembre-se também de adicionar seu DTO e seu Mapper!* 

Antes, visualize como funciona de forma genérica o esquema de autentiação com acesso a um banco de dados.

![Esquema genérico de fluxo de autenticação com acesso ao banco de dados](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH03_F04_Spilca.png)

`User.java`
```java
@Data
@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String password;
    private String authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> this.authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

Para cadastrar novos usuários precisamos de uma rota para receber os dados de username e senha: 

`SignUpController.java`
```java
package br.edu.uepb.coffee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.uepb.coffee.dto.UserDTO;
import br.edu.uepb.coffee.mapper.UserMapper;
import br.edu.uepb.coffee.services.UserService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
@Api(value = "Sign Up")
public class SignUpController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/signup")
    public void signUp(@RequestBody UserDTO userDTO){
        userService.signUpUser(userMapper.convertFromUserDTO(userDTO));
    }
}
```

Note que há o uso de um serviço e de um repositório novos, inclusos por meio das classes a seguir: 

`UserService.java`
```java
package br.edu.uepb.coffee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.uepb.coffee.domain.User;
import br.edu.uepb.coffee.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void signUpUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
```

`UserRepository.java`
```java
package br.edu.uepb.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.uepb.coffee.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    
}
```

Voltando às classes envolvidas no Spring Security, nós iremos modificar o nosso `CustomAuthenticationProvider`, adicionando a busca no banco de dados com o UserDetailsService customizado. Observe o esquema e a implementação, como nas classes a seguir: 

![Uso do AuthenticationProvider na implementação da lógica de busca de usuários no banco de dados.](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH03_F03_Spilca.png)

`UserDetailsServiceImpl.java`
```java
package br.edu.uepb.coffee.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.edu.uepb.coffee.domain.User;
import br.edu.uepb.coffee.repository.UserRepository;

import java.util.Collections;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return User.builder().username(user.getUsername()).password(user.getPassword()).authority(user.getAuthority()).build();
    }
    
}
``` 

`CustomAuthenticationProvider.java`
```java
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailsService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        UserDetails u = userDetailsService.loadUserByUsername(username);
        if (bCryptPasswordEncoder.matches(password, u.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, password, u.getAuthorities());
        } else {
            throw new AuthenticationCredentialsNotFoundException("Error in authentication!");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

## Recuperando informações do usuário logado

Antes de prosseguir para a próxima seção, saiba que é possível fornecer informações do usuário logado, criando um facade para isso, no pacote de componentes: 

`IAuthenticationFacade.java`
```java
public interface IAuthenticationFacade {
    Authentication getAuthenticationInfo();
}
```

`AuthenticationFacade.java`
```java
@Component
public class AuthenticationFacade implements IAuthenticationFacade {
    @Override
    public Authentication getAuthenticationInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
```

Com essa implementação, que é a mais segura e não expõe dados sensíveis nos parâmetros das rotas, é possível implementar um novo recurso para recuperar a informação do usuário logado:

`HelloController.java`
```java
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HelloController {

    private final AuthenticationFacade authenticationFacade;
    
    @GetMapping("/greetings")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok().body("Hello " + authenticationFacade.getAuthenticationInfo().getName() + "!");
    }
}
```

## Reconfigurando a autenticação para uso de Tokens JWT

JSON Web Tokens, conhecidos como JWTs, são usados para formar autorização para usuários. Isso nos ajuda a construir APIs seguras e também é fácil de escalar. Durante a autenticação, um JWT é retornado. Sempre que o usuário deseja acessar um recurso protegido, o navegador deve enviar JWTs no cabeçalho de autorização junto com a solicitação. Uma coisa a entender aqui é que é uma boa prática de segurança proteger a API REST.

Agora precisamos configurar a proteção das rotas, excluindo o controller acima e outras rotas de interesse.

## Efetuando o Login

Para gerenciar o login dos usuários, é preciso ter duas classes importantes: uma para tratar da autenticação (verificação das credenciais e geração do token) e outra para tratar da autorização (verificação do token). As classes abaixo podem ser incluídas no pacote *filters*:

`AuthenticationFilter.java`
```java
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            br.edu.uepb.coffee.domain.User credentials = new ObjectMapper().readValue(request.getInputStream(), 
                                                            br.edu.uepb.coffee.domain.User.class);
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(),new ArrayList<>()));
        } catch (IOException e) {
            throw new RuntimeException("Could not read request" + e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException {
                String token = Jwts.builder()
                                .setSubject(((User) authResult.getPrincipal()).getUsername())
                                .setIssuedAt(new Date(System.currentTimeMillis()))
                                .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                                .signWith(SignatureAlgorithm.HS256, "UEPBProgWeb20202MySecretKeyToGenJWTsToken".getBytes())
                                .compact();
                response.addHeader("Authorization","Bearer " + token);
    }
}
```

`AuthorizationFilter.java`
```java
public class AuthorizationFilter extends BasicAuthenticationFilter {
    
    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        String header = request.getHeader("Authorization");
        
        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        
        if (token != null) {
            String user = Jwts.parser().setSigningKey("UEPBProgWeb20202MySecretKeyToGenJWTsToken".getBytes())
                    .parseClaimsJws(token.replace("Bearer ",""))
                    .getBody()
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }

        return null;
    }
}
```

Por último, basta incluir os filters no arquivo de configuração, dispensando o uso do AuthenticationProvider, como a seguir. Também é possível reforçar a configuração de CORS:

`SecurityConfig.java`
```java
@Configuration
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserDetailsService userDetailsService;
	
	private static final String[] AUTH_WHITELIST = {
        "/v2/api-docs",
        "/signup",
        "/h2-console/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**"
    };

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        // acesso ao Banco de Dados em memória (H2)
        http.cors().and().csrf().disable();
        http.headers().frameOptions().sameOrigin(); 
        http.authorizeRequests()
		.authorizeRequests()
            .antMatchers(AUTH_WHITELIST).permitAll()
            .anyRequest().authenticated()
            .and().addFilter(new AuthenticationFilter(authenticationManager()))
            .addFilter(new AuthorizationFilter(authenticationManager()))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
```

## Demonstração

Para testar seus recursos, tente acessar via Postman (ou outro software de sua preferência) uma rota como nas aulas anteriores. Você se deparará com a mensagem `401 - Unauthorized` como retorno. É preciso se cadastrar.

Para tanto, acesse a rota `/signup` e envie, no corpo da requisição, um username e um password, seguindo o modelo de dados definido no início deste tutorial. 

Em seguida, use o mesmo corpo de requisição chamando a rota `/login`. Você receberá nos headers da resposta um cabeçalho chamado `Authorization`, no formato `Bearer meu_token`. Copie o meu token e adicione ao cabeçalho da sua nova requisição à API `/coffees`. 
