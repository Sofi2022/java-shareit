package ru.practicum.shareit.exceptions;

public class AlreadyExists extends RuntimeException {

    public AlreadyExists(String message) {
        super(message);
    }
}
