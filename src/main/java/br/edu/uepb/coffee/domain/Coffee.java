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
