package io.thedocs.soyuz.validator.test

import spock.lang.Specification

/**
 * Created by fbelov on 22.05.16.
 */
class ActressValidatorSpec extends Specification {

    def "should construct correct validation rules"() {
        when:
        def validator = Actress.validator

        then:
        println(validator.validationData)

        assert validator.validationData
    }

}
