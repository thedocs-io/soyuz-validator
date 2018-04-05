package io.thedocs.soyuz.validator

import io.thedocs.soyuz.validator.test.Actress
import io.thedocs.soyuz.err.Err
import spock.lang.Specification

import java.util.function.Function

/**
 * Created by fbelov on 06.05.16.
 */
class FluentValidatorSpec extends Specification {

    private static final FluentValidator<Actress> actressValidator = Actress.validator

    def "should mix property names"() {
        when:
        def validator = FluentValidator.of("actress.sizes", Actress.Sizes)
                .i("height").greaterThan(4).lessThan(5).b()
                .i("breast").greaterThan(1).b()
                .i("waist").greaterThan(1).lessThan(999).b()
                .build()

        then:
        println(validator.validationData)

        assert validator.validationData
    }

    def "eq (simple)"() {
        when:
        def validator = FluentValidator.of(String).eq("hello world").build()

        then:
        assert validator.validate("hello") == FluentValidator.Result.failure("hello", Err.code("notEq").value("hello").build())
        assert validator.validate("hello world") == FluentValidator.Result.success("hello world")
    }

    def "eq (function)"() {
        when:
        def validator = FluentValidator.of(String).eq({ it == "hello" || it == "world" } as Function).build()

        then:
        assert validator.validate("hello") == FluentValidator.Result.success("hello")
        assert validator.validate("world") == FluentValidator.Result.success("world")
        assert validator.validate("hello world") == FluentValidator.Result.failure("hello world", Err.code("notEq").value("hello world").build())
    }
}
