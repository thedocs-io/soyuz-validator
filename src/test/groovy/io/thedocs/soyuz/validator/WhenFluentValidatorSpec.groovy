package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

import java.util.function.BiFunction

class WhenFluentValidatorSpec extends Specification {

    def "when (simple)"() {
        when:
        def validator = FluentValidator.of(Car)
                .string("title").notEmpty().when({ c, title -> c.power > 100 } as BiFunction).b()
                .build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                | result
        new Car(power: 50)                 | { c -> FluentValidator.Result.success(c) } //skip by when
        new Car(title: "Lada", power: 150) | { c -> FluentValidator.Result.success(c) } //correct title
        new Car(power: 150)                | { c -> FluentValidator.Result.failure(c, Err.field("title").code("notEmpty").build()) }
    }

    static class Car {
        private String title
        private int power

        int getPower() {
            return power
        }

        String getTitle() {
            return title
        }
    }
}
