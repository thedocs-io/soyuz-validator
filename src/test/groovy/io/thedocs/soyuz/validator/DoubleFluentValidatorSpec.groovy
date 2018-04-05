package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class DoubleFluentValidatorSpec extends Specification {

    def "greaterThan"() {
        when:
        def validator = Fv.of(Car).double_("power").greaterThan(50d).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                   | result
        new Car()             | { c -> Fv.Result.success(c) }
        new Car(power: 150.5d) | { c -> Fv.Result.success(c) }
        new Car(power: 50d)    | { c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(50d).params(["criterion": 50d]).build()) }
        new Car(power: 10d)    | { c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(10d).params(["criterion": 50d]).build()) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = Fv.of(Car).double_("power").greaterOrEqual(50d).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                   | result
        new Car()             | { c -> Fv.Result.success(c) }
        new Car(power: 150.7d) | { c -> Fv.Result.success(c) }
        new Car(power: 50d)    | { c -> Fv.Result.success(c) }
        new Car(power: 10d)    | { c -> Fv.Result.failure(c, Err.field("power").code("greaterOrEqual").value(10d).params(["criterion": 50d]).build()) }
    }

    def "lessThan"() {
        when:
        def validator = Fv.of(Car).double_("power").lessThan(999d).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                     | result
        new Car()               | { c -> Fv.Result.success(c) }
        new Car(power: 150.991d) | { c -> Fv.Result.success(c) }
        new Car(power: 999d)     | { c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(999d).params(["criterion": 999d]).build()) }
        new Car(power: 1500d)    | { c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(1500d).params(["criterion": 999d]).build()) }
    }

    def "lessOrEqual"() {
        when:
        def validator = Fv.of(Car).double_("power").lessOrEqual(999d).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                     | result
        new Car()               | { c -> Fv.Result.success(c) }
        new Car(power: 150.345d) | { c -> Fv.Result.success(c) }
        new Car(power: 998.999d) | { c -> Fv.Result.success(c) }
        new Car(power: 999d)     | { c -> Fv.Result.success(c) }
        new Car(power: 1500.5d)  | { c -> Fv.Result.failure(c, Err.field("power").code("lessOrEqual").value(1500.5d).params(["criterion": 999d]).build()) }
    }

    def "greaterThan+lessThan"() {
        when:
        def validator = Fv.of(Car).double_("power").greaterThan(1d).lessThan(999d).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                     | result
        new Car(power: -1.234d)  | { c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(-1.234d).params(["criterion": 1d]).build()) }
        new Car(power: 1500.43d) | { c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(1500.43d).params(["criterion": 999d]).build()) }
        new Car(power: 150.6d)   | { c -> Fv.Result.success(c) }
    }

    static class Car {
        private String title
        private Double power

        Double getPower() {
            return power
        }

        String getTitle() {
            return title
        }
    }
}
