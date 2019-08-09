package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SelfTest {

    @Test
    public void shouldValidateString() {
        //when:
        Fv.Validator<String> validator = Fv.of(String.class).self().string().notBlank().email().and().build();

        //then:
        assertEquals(Fv.Result.failure(" ", Err.code("notBlank").value(" ").build()), validator.validate(" "));
        assertEquals(Fv.Result.failure("abc", Err.code("email").value("abc").build()), validator.validate("abc"));
        assertEquals(Fv.Result.success("a@a.ru"), validator.validate("a@a.ru"));
    }

}
