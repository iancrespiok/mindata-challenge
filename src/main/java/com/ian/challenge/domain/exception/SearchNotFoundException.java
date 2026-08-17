package com.ian.challenge.domain.exception;

public class SearchNotFoundException extends RuntimeException {
    public SearchNotFoundException(String searchId) {
        super("It does not exist a search with searchId = " + searchId);
    }
}
