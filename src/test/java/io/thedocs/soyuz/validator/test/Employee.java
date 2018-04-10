package io.thedocs.soyuz.validator.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created on 06.04.18.
 */
@AllArgsConstructor
@Getter
@Builder
@ToString
public class Employee {
    private int id;
    private String email;
    private String name;
    private Integer age;
}
