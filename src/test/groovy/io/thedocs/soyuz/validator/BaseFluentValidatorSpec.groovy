package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

import java.util.function.Function
/**
 * Created by fbelov on 06.05.16.
 */
class BaseFluentValidatorSpec extends Specification {

    def "eq (simple)"() {
        when:
        def validator = Fv.of(String).eq("hello world").build()

        then:
        assert validator.validate("hello") == Fv.Result.failure("hello", Err.code("notEq").value("hello").build())
        assert validator.validate("hello world") == Fv.Result.success("hello world")
    }

    def "eq (function)"() {
        when:
        def validator = Fv.of(String).eq({ it == "hello" || it == "world" } as Function).build()

        then:
        assert validator.validate("hello") == Fv.Result.success("hello")
        assert validator.validate("world") == Fv.Result.success("world")
        assert validator.validate("hello world") == Fv.Result.failure("hello world", Err.code("notEq").value("hello world").build())
    }
}
