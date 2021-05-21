package br.edu.uepb.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.uepb.coffee.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    
}
