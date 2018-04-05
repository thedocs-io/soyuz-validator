package io.thedocs.soyuz.validator

import io.thedocs.soyuz.err.Err
import spock.lang.Specification

class StringFluentValidatorSpec extends Specification {

    def "notEmpty"() {
        when:
        def validator = Fv.of(Car).string("title").notEmpty().b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                    | result
        new Car()              | { c -> Fv.Result.failure(c, Err.field("title").code("notEmpty").build()) }
        new Car(title: "")     | { c -> Fv.Result.failure(c, Err.field("title").code("notEmpty").value("").build()) }
        new Car(title: "Lada") | { c -> Fv.Result.success(c) }
    }

    def "isBoolean"() {
        setup:
        def validator = Fv.of(Car).string("title").isBoolean().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isBoolean").value(title).build())
        }

        where:
        title   | result
        null    | false
        ""      | false
        "abc"   | false
        "true"  | true
        "tRue"  | true
        "false" | true
        "FALSE" | true
    }

    def "isByte"() {
        setup:
        def validator = Fv.of(Car).string("title").isByte().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isByte").value(title).build())
        }

        where:
        title  | result
        null   | false
        ""     | false
        "abc"  | false
        "-129" | false
        "-128" | true
        "0"    | true
        "127"  | true
        "128"  | false
        "12a8" | false
    }

    def "isShort"() {
        setup:
        def validator = Fv.of(Car).string("title").isShort().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isShort").value(title).build())
        }

        where:
        title    | result
        null     | false
        ""       | false
        "abc"    | false
        "-32769" | false
        "-32768" | true
        "0"      | true
        "32767"  | true
        "32768"  | false
        "3276a8" | false
    }

    def "isInteger"() {
        setup:
        def validator = Fv.of(Car).string("title").isInteger().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isInteger").value(title).build())
        }

        where:
        title         | result
        null          | false
        ""            | false
        "abc"         | false
        "-2147483649" | false
        "-2147483648" | true
        "0"           | true
        "2147483647"  | true
        "2147483648"  | false
        "21474836a48" | false
    }

    def "isLong"() {
        setup:
        def validator = Fv.of(Car).string("title").isLong().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isLong").value(title).build())
        }

        where:
        title                  | result
        null                   | false
        ""                     | false
        "abc"                  | false
        "-9223372036854775809" | false
        "-9223372036854775808" | true
        "0"                    | true
        "9223372036854775807"  | true
        "9223372036854775808"  | false
        "9223372036854a775808" | false
    }

    def "isFloat"() {
        setup:
        def validator = Fv.of(Car).string("title").isFloat().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isFloat").value(title).build())
        }

        where:
        title     | result
        null      | false
        ""        | false
        "abc"     | false
        "-19.95a" | false
        "-19.95"  | true
        "0"       | true
        "19.95"   | true
        "19.a95"  | false
    }

    def "isDouble"() {
        setup:
        def validator = Fv.of(Car).string("title").isDouble().b().build()

        expect:
        def car = new Car(title: title)
        def answer = validator.validate(car)

        if (result) {
            assert answer == Fv.Result.success(car)
        } else {
            assert answer == Fv.Result.failure(car, Err.field("title").code("isDouble").value(title).build())
        }

        where:
        title     | result
        null      | false
        ""        | false
        "abc"     | false
        "-19.95a" | false
        "-19.95"  | true
        "0"       | true
        "19.95"   | true
        "19.a95"  | false
    }

    def "greaterThan"() {
        when:
        def validator = Fv.of(Car).string("title").greaterThan(2).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                 | result
        new Car(title: "pretty long title") | { c -> Fv.Result.success(c) }
        new Car(title: "ok!")               | { c -> Fv.Result.success(c) }
        new Car(title: "ab")                | { c -> Fv.Result.failure(c, Err.field("title").code("greaterThan").value("ab").params(["criterion": 2]).build()) }
        new Car(title: "")                  | { c -> Fv.Result.failure(c, Err.field("title").code("greaterThan").value("").params(["criterion": 2]).build()) }
        new Car(title: null)                | { c -> Fv.Result.failure(c, Err.field("title").code("greaterThan").value(null).params(["criterion": 2]).build()) }
    }

    def "greaterOrEqual"() {
        when:
        def validator = Fv.of(Car).string("title").greaterOrEqual(2).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                                 | result
        new Car(title: "pretty long title") | { c -> Fv.Result.success(c) }
        new Car(title: "ok!")               | { c -> Fv.Result.success(c) }
        new Car(title: "ab")                | { c -> Fv.Result.success(c) }
        new Car(title: "a")                 | { c -> Fv.Result.failure(c, Err.field("title").code("greaterOrEqual").value("a").params(["criterion": 2]).build()) }
        new Car(title: "")                  | { c -> Fv.Result.failure(c, Err.field("title").code("greaterOrEqual").value("").params(["criterion": 2]).build()) }
        new Car(title: null)                | { c -> Fv.Result.failure(c, Err.field("title").code("greaterOrEqual").value(null).params(["criterion": 2]).build()) }
    }

    def "lessThan"() {
        when:
        def validator = Fv.of(Car).string("title").lessThan(2).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                   | result
        new Car(title: null)  | { c -> Fv.Result.success(c) }
        new Car(title: "")    | { c -> Fv.Result.success(c) }
        new Car(title: "a")   | { c -> Fv.Result.success(c) }
        new Car(title: "ab")  | { c -> Fv.Result.failure(c, Err.field("title").code("lessThan").value("ab").params(["criterion": 2]).build()) }
        new Car(title: "abc") | { c -> Fv.Result.failure(c, Err.field("title").code("lessThan").value("abc").params(["criterion": 2]).build()) }
    }

    def "lessOrEqual"() {
        when:
        def validator = Fv.of(Car).string("title").lessOrEqual(2).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                   | result
        new Car(title: null)  | { c -> Fv.Result.success(c) }
        new Car(title: "")    | { c -> Fv.Result.success(c) }
        new Car(title: "a")   | { c -> Fv.Result.success(c) }
        new Car(title: "ab")  | { c -> Fv.Result.success(c) }
        new Car(title: "abc") | { c -> Fv.Result.failure(c, Err.field("title").code("lessOrEqual").value("abc").params(["criterion": 2]).build()) }
    }

    def "greaterThan+lessThan"() {
        when:
        def validator = Fv.of(Car).integer("power").greaterThan(1).lessThan(999).b().build()

        then:
        assert validator.validate(car) == result(car)

        where:
        car                  | result
        new Car(power: -1)   | { c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(-1).params(["criterion": 1]).build()) }
        new Car(power: 1500) | { c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(1500).params(["criterion": 999]).build()) }
        new Car(power: 150)  | { c -> Fv.Result.success(c) }
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
