package com.rigel.ExpenseTracker.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ControlHelper {

    public Pageable createPagination(Integer currentPage, Integer perPage, int size) {
        Pageable pageable;
        if((currentPage != null && perPage != null) && (currentPage > 0 && perPage > 0)){
            pageable = PageRequest.of(currentPage - 1, perPage);
        } else if (currentPage == null && perPage == null){
            pageable = PageRequest.of(0, size);
        } else {
            throw new ResponseStatusException(BAD_REQUEST,"The value of currentPage and/or perPage parameters cannot be under or equal to 0.");
        }
        return pageable;
    }

}
