package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidatorShareTest {

    @Test
    public void shouldShareSelfPropertyValidator() {
        //setup:
        Fv.Validator<String> emailValidator = Fv.of(String.class).self().string().notBlank().email().and().build();
        Fv.Validator<User> userValidator = Fv.of(User.class)
                .self().notNull().and()
                .string("email").validator(emailValidator).and()
                .build();

        //when:
        User user = null;

        //then:
        assertEquals(Fv.Result.failure(null, Errors.reject(Err.code("notNull").build(), Err.field("email").code("notBlank").build())), userValidator.validate(user));

        //when:
        user = new User(" ");

        //then:
        assertEquals(Fv.Result.failure(user, Err.field("email").code("notBlank").value(" ").build()), userValidator.validate(user));

        //when:
        user = new User("qwe123");

        //then:
        assertEquals(Fv.Result.failure(user, Err.field("email").code("email").value("qwe123").build()), userValidator.validate(user));

        //when:
        user = new User("a@a.ru");

        //then:
        assertEquals(Fv.Result.success(user), userValidator.validate(user));
    }


    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class User {
        private String email;
    }

}
