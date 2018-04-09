package io.thedocs.soyuz.validator.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Set;

/**
 * Created by fbelov on 05.04.18.
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Company {
    private int id;
    private String name;
    private Set<Employee> employees;
    private Address address;
    private WorkingHours workingHours;

    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    public static class Address {
        private String city;
        private String location;
    }

    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    public static class WorkingHours {
        private LocalTime from;
        private LocalTime to;
    }
}
