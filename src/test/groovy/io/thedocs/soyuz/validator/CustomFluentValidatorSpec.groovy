package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class CustomFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def validator = FluentValidator.of(Car).i("power").custom(
                { c, power ->
                    if (power > 100) {
                        return FluentValidator.CustomResult.success()
                    } else {
                        return FluentValidator.CustomResult.failure("min")
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car(power: 150) | { c -> FluentValidator.Result.success(c) }
        new Car(power: 90)  | { c -> FluentValidator.Result.failure(c, Err.field("power").code("min").value(90).build()) }
    }

    def "collection"() {
        when:
        def validator = FluentValidator.of(Car).collection("wheels").custom({ c, wheels ->
            if (wheels.size() != 4) {
                return FluentValidator.CustomResult.failure("wrongNumber")
            } else {
                return FluentValidator.CustomResult.success()
            }
        } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                                   | result
        new Car(wheels: [new Wheel(), new Wheel(), new Wheel(), new Wheel()]) | { c -> FluentValidator.Result.success(c) }
        new Car(wheels: [new Wheel()])                                        | { c -> FluentValidator.Result.failure(c, Err.field("wheels").code("wrongNumber").value(c.wheels).build()) }
    }

    def "deep"() {
        when:
        def validator = FluentValidator.of(Car).collection("engine").custom({ c, engine, validator ->
            return FluentValidator.CustomResult.from(
                    validator
                            .string("title").notEmpty().b()
                            .build()
                            .validate(engine)
            )
        } as FluentValidatorObjects.CustomValidator.WithBuilder).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                       | result
        new Car()                                 | { c -> FluentValidator.Result.failure(c, Err.field("engine.title").code("notEmpty").build()) }
        new Car(engine: new Engine(title: "bmw")) | { c -> FluentValidator.Result.success(c) }
    }

    static class Car {
        String title
        int power
        Engine engine
        List<Wheel> wheels
    }

    static class Wheel {
        String position
    }

    static class Engine {
        String title
    }
}
