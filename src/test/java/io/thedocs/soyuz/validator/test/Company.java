package io.thedocs.soyuz.validator.test;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * Created by fbelov on 05.04.18.
 */
@Getter
@AllArgsConstructor
public class Company {
    private int id;
    private String name;
    private Set<Integer> employeeIds;
}
