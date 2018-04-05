package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import io.thedocs.soyuz.err.Errors
import spock.lang.Specification

class ValidatorFluentValidatorSpec extends Specification {

    def "simple"() {
        when:
        def engineValidator = Fv.of(CarEngine)
                .string("title").notEmpty().b()
                .primitiveInt("power").greaterThan(1).lessThan(999).b()
                .build()

        def carValidator = Fv.of(Car)
                .string("title").notEmpty().b()
                .object("engine", CarEngine).notNull().validator(engineValidator).b()
                .build()


        then:
        assert carValidator.validate(car) == result(car)

        where:
        car                                                                    | result
        new Car()                                                              | { c ->
            Fv.Result.failure(
                    c,
                    Errors.reject(
                            Err.field("title").code("notEmpty").build(),
                            Err.field("engine").code("notNull").build(),
                    )
            )
        }

        new Car(title: "", engine: new CarEngine())                            | { c ->
            Fv.Result.failure(
                    c,
                    Errors.reject(
                            Err.field("title").code("notEmpty").value("").build(),
                            Err.field("engine.title").code("notEmpty").build(),
                            Err.field("engine.power").code("greaterThan").value(0).params(["criterion": 1]).build(),
                    )
            )
        }

        new Car(title: "Lada", engine: new CarEngine(title: "v8", power: 113)) | { c -> Fv.Result.success(c) }
    }

    static class Car {
        private String title
        private CarEngine engine

        String getTitle() {
            return title
        }

        CarEngine getEngine() {
            return engine
        }
    }

    static class CarEngine {
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
