package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import io.thedocs.soyuz.err.Errors
import spock.lang.Ignore
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by fbelov on 06.05.16.
 */
@Ignore
class FailFastFluentValidatorSpec extends Specification {

    def "should fail fast"() {
        setup:
        def car
        def result
        def checked = new AtomicBoolean()
        def validator = getValidator(false, checked)

        when:
        checked.set(false)
        car = new Car(title: "Lada", power: 150)
        result = validator.validate(car)

        then:
        assert result == Fv.Result.success(car)
        assert checked.get() == true

        when:
        checked.set(false)
        car = new Car(power: 90)
        result = validator.validate(car)

        then:
        assert result == Fv.Result.failure(car, Errors.reject(Err.field("title").code("notEmpty").build(), Err.field("power").code("min").value(90).build()))
        assert checked.get() == true

        when:
        checked.set(false)
        car = new Car(power: 90)
        result = getValidator(true, checked).validate(car)

        then:
        assert result == Fv.Result.failure(car, Err.field("title").code("notEmpty").build())
        assert checked.get() == false
    }

    private getValidator(boolean failFast, AtomicBoolean powerChecked) {
        return Fv.of(Car)
                .failFast(failFast)
                .string("title").notEmpty().b()
                .primitiveInt("power").custom(
                { car, power ->
                    powerChecked.set(true)

                    if (power > 100) {
                        return Fv.CustomResult.success()
                    } else {
                        return Fv.CustomResult.failure(Err.code("min").value(power).build())
                    }
                } as FluentValidatorObjects.CustomValidator.Simple).b()
                .build()
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
