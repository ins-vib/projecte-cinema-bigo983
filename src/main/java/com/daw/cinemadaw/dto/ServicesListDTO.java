package com.daw.cinemadaw.dto;

import java.util.ArrayList;
import java.util.List;

public class ServicesListDTO {

    private List<String> services = new ArrayList<>();

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }
}