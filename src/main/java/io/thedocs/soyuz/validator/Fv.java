package io.thedocs.soyuz.validator;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import lombok.*;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fbelov on 28.04.16.
 */
public interface Fv {

    //https://github.com/JeremySkinner/FluentValidation

    static <T> FluentValidatorBuilder<T> of(Class<T> clazz) {
        return new FluentValidatorBuilder<>();
    }

    static <T> FluentValidatorBuilder<T> of(String property, Class<T> clazz) {
        return new FluentValidatorBuilder<>(property);
    }

    interface Validator<T> {
        Fv.Result<T> validate(T rootObject);
    }

    /**
     * Created by fbelov on 07.06.16.
     */
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    @ToString
    class CustomResult {
        private Errors errors;

        public boolean isOk() {
            return errors.isOk();
        }

        public boolean hasErrors() {
            return errors.hasErrors();
        }

        public static CustomResult success() {
            return new CustomResult(Errors.ok());
        }

        public static CustomResult failure(String code) {
            return failure(Err.code(code).build());
        }

        public static CustomResult failure(Err error) {
            return new CustomResult(Errors.reject(error));
        }

        public static CustomResult failure(Errors errors) {
            return new CustomResult(errors);
        }

        public static CustomResult from(Result result) {
            if (result.isOk()) {
                return success();
            } else {
                return failure(result.getErrors());
            }
        }
    }

    @ToString
    @EqualsAndHashCode
    class Result<R> {
        private static final Result SUCCESS = new Result(null, Errors.ok());

        private R rootObject;
        private Errors errors;

        private Result(R rootObject, Errors errors) {
            this.rootObject = rootObject;
            this.errors = errors;
        }

        public boolean isOk() {
            return errors.isOk();
        }

        public boolean hasErrors() {
            return errors.hasErrors();
        }

        public R getRootObject() {
            return rootObject;
        }

        public Errors getErrors() {
            return errors;
        }

        public static Result success() {
            return SUCCESS;
        }

        public static <R> Result<R> success(R rootObject) {
            return new Result<>(rootObject, Errors.ok());
        }

        public static <R> Result<R> failure(R rootObject, Err error) {
            return new Result<>(rootObject, Errors.reject(error));
        }

        public static <R> Result<R> failure(R rootObject, Errors errors) {
            return new Result<>(rootObject, errors);
        }

        public void ifHasErrorsThrowAnException() {
            if (hasErrors()) {
                throw new BeanValidationException(rootObject, errors);
            }
        }

        public void ifHasErrorsThrowAnExceptionOr(Runnable runnable) {
            ifHasErrorsThrowAnException();

            runnable.run();
        }

        public void ifHasErrorsThrowAnExceptionOr(Consumer<R> consumer) {
            ifHasErrorsThrowAnException();

            consumer.accept(rootObject);
        }

        public <T> T ifHasErrorsThrowAnExceptionOr(Supplier<T> supplier) {
            ifHasErrorsThrowAnException();

            return supplier.get();
        }

        public <T> T ifHasErrorsThrowAnExceptionOr(Function<R, T> function) {
            ifHasErrorsThrowAnException();

            return function.apply(rootObject);
        }
    }

    @Getter
    @ToString
    class BeanValidationException extends RuntimeException {
        private Object rootObject;
        private Errors errors;

        public BeanValidationException(Object rootObject, Errors errors) {
            super("{root=" + rootObject + ", errors=" + errors + "}");

            this.rootObject = rootObject;
            this.errors = errors;
        }

        public BeanValidationException(Object rootObject, Err error) {
            this(rootObject, Errors.reject(error));
        }

    }

}
