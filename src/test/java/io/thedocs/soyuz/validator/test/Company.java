package io.thedocs.soyuz.validator.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Created by fbelov on 05.04.18.
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Company {
    private int id;
    private String name;
    private List<Employee> employees;
    private Address address;
    private WorkingHours workingHours;

    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    @ToString
    public static class Address {
        private String city;
        private String location;
    }

    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    @ToString
    public static class WorkingHours {
        private LocalTime from;
        private LocalTime to;
    }
}
