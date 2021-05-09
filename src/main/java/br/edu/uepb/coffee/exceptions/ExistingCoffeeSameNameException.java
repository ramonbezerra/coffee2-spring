package br.edu.uepb.coffee.exceptions;

public class ExistingCoffeeSameNameException extends Exception {
    public ExistingCoffeeSameNameException(String message) {
        super(message);
    }
}
