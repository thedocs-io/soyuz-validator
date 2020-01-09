# Fluent validator
Are you a `Hibernate` fan? This project is not for you.
Are you a `JOOQ` fan? Good, spend few minutes, maybe you will like it.

## Purpose
This java library allows you to **code** your validation logic (using builder patter).
It scales from simple validators to very complex ones

## Simple example
Let's define our model - `User` object with `email` property:
```java
@AllArgsConstructor
@Getter
public static class User {
    private String email;
}
```

We want to check that email is not empty and valid. Let's define our validator:
```java
Fv.Validator<User> userValidator = Fv.of(User.class)
        .string("email").notEmpty().email().b()
        .build();
```

and validate our `User` object:
```java
User user = new User("trololo");
Fv.Result<User> validationResult = userValidator.validate(user);
    
assertEquals(Fv.Result.failure(user, Err.code("email").field("email").value("trololo").build()), validationResult);
```

[Click here to read simple example](https://github.com/thedocs-io/soyuz-validator/blob/master/src/test/java/io/thedocs/soyuz/validator/test/SimpleTest.java)

## Validation builder
Start building your validator with `Fv.of(classOfTheObjectYouAreGoingToValidate.class)`.
Use `.string("string property")` / `.integer("integer property")` / etc to start coding property validation.
Use `.b()` (alias to back) to return to main validation builder.

## Complex example
Fluent validator is very flexible. It's hard to explain all features - just check the code - it should be pretty easy to understand.
We created the [special test](https://github.com/thedocs-io/soyuz-validator/blob/master/src/test/java/io/thedocs/soyuz/validator/test/SpringDependencyObjectValidationTest.java) which demonstrates a lot of complex features of the FluentValidator:

1. Dependency injection to validator - you can inject your dao / service object and check that there are no Users with such email address
2. Sub-object validation with another validator - you can share your validators with each other
3. Custom validation - you can implement any validation logic you want and return `Fv.CustomResult`
4. Validation collection of objects with item validator

[Click here to read complex example](https://github.com/thedocs-io/soyuz-validator/blob/master/src/test/java/io/thedocs/soyuz/validator/test/SpringDependencyObjectValidationTest.java)

## How to use
### Maven
```
<dependency>
    <groupId>io.thedocs</groupId>
    <artifactId>soyuz-validator</artifactId>
    <version>2.03</version>
</dependency>
```

### Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'io.thedocs:soyuz-validator:3.0.5'
}
```

## License
MIT