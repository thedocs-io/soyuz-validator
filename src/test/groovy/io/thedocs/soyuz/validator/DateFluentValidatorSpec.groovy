package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class DateFluentValidatorSpec extends Specification {

    def "greaterThan / lessThan"() {
        when:
        def validator = Fv.of(Car)
                .date("constructed").greaterThan(new Date(1987 - 1900, 01, 01)).lessThan(new Date(1987 - 1900, 12, 31)).b()
                .build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                 | result
        new Car()                                           | { c -> Fv.Result.success(c) }
        new Car(constructed: new Date(1986 - 1900, 12, 31)) | { c -> Fv.Result.failure(c, Err.field("constructed").code("greaterThan").value(c.constructed).build()) }
        new Car(constructed: new Date(1988 - 1900, 01, 01)) | { c -> Fv.Result.failure(c, Err.field("constructed").code("lessThan").value(c.constructed).build()) }
        new Car(constructed: new Date(1987 - 1900, 05, 01)) | { c -> Fv.Result.success(c) }
    }

    def "between"() {
        when:
        def validator = Fv.of(Car)
                .date("constructed").between(new Date(1987 - 1900, 01, 01), new Date(1987 - 1900, 12, 31)).b()
                .build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                 | result
        new Car()                                           | { c -> Fv.Result.success(c) }
        new Car(constructed: new Date(1986 - 1900, 12, 31)) | { c -> Fv.Result.failure(c, Err.field("constructed").code("between").value(c.constructed).build()) }
        new Car(constructed: new Date(1988 - 1900, 01, 01)) | { c -> Fv.Result.failure(c, Err.field("constructed").code("between").value(c.constructed).build()) }
        new Car(constructed: new Date(1987 - 1900, 05, 01)) | { c -> Fv.Result.success(c) }
    }

    static class Car {
        String title
        Date constructed
    }
}
