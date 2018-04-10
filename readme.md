# Fluent validator
Are you a Hibernate fan? This project is not for you.
Are you a JOOQ fan? Good, spend few minutes, may be you will like it.

## Purpose
This project allows you to **code** your validation logic.
It scales from simple validators to very complex ones (@see io.thedocs.soyuz.validator.test.SpringDependencyObjectValidationTest)

## Simple example
Suppose you've got User object with email property and you want to check that it's not empty and it's valid email:

## Validation builder
Start building your validator from. Use string("string property") / integer("int property") / etc to start coding property validation.
Use .b() (aka back) to return to main validation builder.

## Complex example
Here is an example of complex validator with comments (copied from io.thedocs.soyuz.validator.test.SpringDependencyObjectValidationTest):