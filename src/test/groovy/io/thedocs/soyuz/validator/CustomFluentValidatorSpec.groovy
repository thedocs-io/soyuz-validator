package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class CustomFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def validator = Fv.of(Car).primitiveInt("power").custom(
                { c, power ->
                    if (power > 100) {
                        return Fv.CustomResult.success()
                    } else {
                        return Fv.CustomResult.failure("min")
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                 | result
        new Car(power: 150) | { c -> Fv.Result.success(c) }
        new Car(power: 90)  | { c -> Fv.Result.failure(c, Err.field("power").code("min").value(90).build()) }
    }

    def "collection"() {
        when:
        def validator = Fv.of(Car).collection("wheels").custom({ c, wheels ->
            if (wheels.size() != 4) {
                return Fv.CustomResult.failure("wrongNumber")
            } else {
                return Fv.CustomResult.success()
            }
        } as FluentValidatorObjects.CustomValidator.Simple).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                                                   | result
        new Car(wheels: [new Wheel(), new Wheel(), new Wheel(), new Wheel()]) | { c -> Fv.Result.success(c) }
        new Car(wheels: [new Wheel()])                                        | { c -> Fv.Result.failure(c, Err.field("wheels").code("wrongNumber").value(c.wheels).build()) }
    }

    def "deep"() {
        when:
        def validator = Fv.of(Car).collection("engine").customWithBuilder({ c, engine, validator ->
            return Fv.CustomResult.from(
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
        new Car()                                 | { c -> Fv.Result.failure(c, Err.field("engine.title").code("notEmpty").build()) }
        new Car(engine: new Engine(title: "bmw")) | { c -> Fv.Result.success(c) }
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
