# Fluent validator
Are you a `Hibernate` fan? This project is not for you.
Are you a `JOOQ` fan? Good, spend few minutes, maybe you will like it.

## Purpose
This project allows you to **code** your validation logic (using builder patter).
It scales from simple validators to very complex ones

## Simple example
Suppose you've got `User` object with `email` property and you want to check that it's not empty and it's valid email:

```java
    @AllArgsConstructor
    @Getter
    public static class User {
        private String email;
    }

    public static void main(String[] args) {
        Fv.Validator<User> userValidator = Fv.of(User.class)
                .string("email").notEmpty().email().b()
                .build();

        User user = new User("trololo");
        Fv.Result<User> validationResult = userValidator.validate(user);

        System.out.println(validationResult);
    }
```

## Validation builder
Start building your validator with `Fv.of(classOfTheObjectYouAreGoingToValidate.class)`.
Use `.string("string property")` / `.integer("integer property")` / etc to start coding property validation.
Use `.b()` (alias to back) to return to main validation builder.

## Complex example
We created the special test which demonstrates a lot of complex features of the FluentValidator:

1. Dependency injection to validator - you can inject your dao / service object and check that there are no Users with such email address
2. Sub-object validation with another validator - you can share your validators with each other
3. Custom validation - you can implement any validation logic you want and return `Fv.CustomResult`
4. Validation collection of objects with item validator

[Click here to get complex example](https://github.com/thedocs-io/soyuz-validator/blob/master/src/test/java/io/thedocs/soyuz/validator/test/SpringDependencyObjectValidationTest.java)