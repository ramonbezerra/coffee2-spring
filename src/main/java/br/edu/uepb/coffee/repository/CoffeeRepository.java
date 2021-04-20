package br.edu.uepb.coffee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.uepb.coffee.domain.Coffee;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    
}
