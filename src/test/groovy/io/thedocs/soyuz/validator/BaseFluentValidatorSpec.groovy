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
        def validator = Fv.of(String).self().eq("hello world").b().build()

        then:
        assert validator.validate("hello") == Fv.Result.failure("hello", Err.code("notEq").value("hello").build())
        assert validator.validate("hello world") == Fv.Result.success("hello world")
    }

    def "eq (function)"() {
        when:
        def validator = Fv.of(String).self().eq({ it == "hello" || it == "world" } as Function).b().build()

        then:
        assert validator.validate("hello") == Fv.Result.success("hello")
        assert validator.validate("world") == Fv.Result.success("world")
        assert validator.validate("hello world") == Fv.Result.failure("hello world", Err.code("notEq").value("hello world").build())
    }

    def "notNull"() {
        when:
        def validator = Fv.of(String).self().notNull().b().build()

        then:
        assert validator.validate(null) == Fv.Result.failure(null, Err.code("notNull").value(null).build())
        assert validator.validate("") == Fv.Result.success("")
        assert validator.validate("abc") == Fv.Result.success("abc")
    }
}
