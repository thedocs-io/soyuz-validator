package io.thedocs.soyuz.validator.test;

import com.fasterxml.jackson.annotation.JsonValue;
import io.thedocs.soyuz.TruthyCastableI;
import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapFromTest {

    Fv.Validator<User> userValidator = Fv.of(User.class)
            .string("email").
                    mapFrom(Email.class, Email::asString).
                    notEmpty().
                    email().
                    when((u, email) -> email == null || !email.equals("abc")).
                    b()
            .build();

    @Test
    public void shouldMapFromEmailToString() {
        //when:
        User user = new User(null);

        //then:
        assertEquals(Fv.Result.failure(user, Err.code("notEmpty").field("email").value(null).build()), userValidator.validate(user));

        //when:
        user = new User(new Email("123"));

        //then:
        assertEquals(Fv.Result.failure(user, Err.code("email").field("email").value("123").build()), userValidator.validate(user));

        //when:
        user = new User(new Email("a@a.ru"));

        //then:
        assertEquals(Fv.Result.success(user), userValidator.validate(user));
    }

    @Test
    public void shouldMapBeforeWhen() {
        //when:
        User user = new User(new Email("abc"));

        //then:
        assertEquals(Fv.Result.success(user), userValidator.validate(user));
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class User {
        private Email email;
    }

    @EqualsAndHashCode
    public class Email implements TruthyCastableI {
        private String mail;

        public Email(String mail) {
            this.mail = (mail != null) ? mail.toLowerCase() : null;
        }

        @JsonValue
        public String asString() {
            return mail;
        }

        @Override
        public String toString() {
            return mail;
        }

        @Override
        public boolean asTruthy() {
            return is.tt(mail);
        }
    }

}
