package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created on 16.04.18.
 */
public class SimpleTest {

    @AllArgsConstructor
    @Getter
    public static class User {
        private String email;
    }


    Fv.Validator<User> userValidator = Fv.of(User.class)
            .string("email").notEmpty().email().b()
            .build();


    @Test
    public void shouldReturnErrorForEmptyEmail() {
        User user = new User("");

        assertEquals(Fv.Result.failure(user, Err.code("notEmpty").field("email").value("").build()), userValidator.validate(user));
    }

    @Test
    public void shouldReturnErrorForInvalidEmail() {
        User user = new User("trololo");
        Fv.Result<User> validationResult = userValidator.validate(user);

        assertEquals(Fv.Result.failure(user, Err.code("email").field("email").value("trololo").build()), validationResult);
    }

    @Test
    public void shouldReturnSuccessForCorrectEmail() {
        User user = new User("trololo@gmail.com");

        assertEquals(Fv.Result.success(user), userValidator.validate(user));
    }
}
