package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class IntFluentValidatorSpec extends Specification {

    def "greaterThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterThan(1).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.failure(c, Err.field("power").code("greaterThan").value(0).params([criterion: 1]).build()) }
        new Car(power: 1)   | { c -> FluentValidator.Result.failure(c, Err.field("power").code("greaterThan").value(1).params([criterion: 1]).build()) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterOrEqual(1).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car()           | { c -> FluentValidator.Result.failure(c, Err.field("power").code("greaterOrEqual").value(0).params([criterion: 1]).build()) }
        new Car(power: 1)   | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
    }

    def "lessThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, Err.field("power").code("lessThan").value(1500).params([criterion: 999]).build()) }
        new Car(power: 999)  | { c -> FluentValidator.Result.failure(c, Err.field("power").code("lessThan").value(999).params([criterion: 999]).build()) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    def "lessOrEqual"() {
        when:
        def validator = FluentValidator.of(Car).i("power").lessOrEqual(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, Err.field("power").code("lessOrEqual").value(1500).params([criterion: 999]).build()) }
        new Car(power: 999)  | { c -> FluentValidator.Result.success(c) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
    }

    def "greaterThan+lessThan"() {
        when:
        def validator = FluentValidator.of(Car).i("power").greaterThan(1).lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> FluentValidator.Result.failure(c, Err.field("power").code("greaterThan").value(-1).params([criterion: 1]).build()) }
        new Car(power: 1500) | { c -> FluentValidator.Result.failure(c, Err.field("power").code("lessThan").value(1500).params([criterion: 999]).build()) }
        new Car(power: 150)  | { c -> FluentValidator.Result.success(c) }
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
