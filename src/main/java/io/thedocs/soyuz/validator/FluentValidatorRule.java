package io.thedocs.soyuz.validator;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 22.05.16.
 */
public interface FluentValidatorRule<R, V> {

    Fv.Result validate(R rootObject, String property, V value);

    abstract class AbstractRule<R, V> implements FluentValidatorRule<R, V> {

        public Fv.Result validate(R rootObject, String property, V value) {
            if (!isValid(rootObject, value)) {
                return Fv.Result.failure(rootObject, Err.field(property).code(getCode()).value(value).params(getErrorParams()).build());
            } else {
                return null;
            }
        }

        @Nullable
        public Map<String, Object> getErrorParams() {
            return null;
        }

        protected abstract String getCode();

        protected abstract boolean isValid(R rootObject, V value);
    }

    interface Str {
        class NotEmpty<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "notEmpty";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && value.length() > 0;
            }
        }

        class NotBlank<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "notBlank";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && value.trim().length() > 0;
            }
        }

        class Url<R> extends AbstractRule<R, String> {

            @Override
            protected String getCode() {
                return "url";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value == null || isUrl(value);
            }

            private boolean isUrl(String value) {
                try {
                    new URL(value);

                    return true;
                } catch (MalformedURLException e) {
                    return false;
                }
            }
        }

        class Email<R> extends AbstractRule<R, String> {

            private static final Pattern MAIL_PATTERN = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

            @Override
            protected String getCode() {
                return "email";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value == null || MAIL_PATTERN.matcher(value).matches();
            }
        }

        class IsBoolean<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isBoolean";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
            }
        }

        class IsByte<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isByte";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Byte.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class IsShort<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isShort";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Short.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class IsInteger<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isInteger";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Integer.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class IsLong<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isLong";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Long.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class IsDouble<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isDouble";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Double.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class IsFloat<R> extends AbstractRule<R, String> {
            @Override
            protected String getCode() {
                return "isFloat";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                if (value == null) return false;

                try {
                    Float.valueOf(value);

                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        class GreaterOrEqual<R> extends AbstractRule<R, String> {
            private int size;

            public GreaterOrEqual(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "greaterOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && value.length() >= size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class GreaterThan<R> extends AbstractRule<R, String> {
            private int size;

            public GreaterThan(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "greaterThan";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value != null && value.length() > size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class LessOrEqual<R> extends AbstractRule<R, String> {
            private int size;

            public LessOrEqual(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "lessOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value == null || value.length() <= size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class LessThan<R> extends AbstractRule<R, String> {
            private int size;

            public LessThan(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "lessThan";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value == null || value.length() < size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class Matches<R> extends AbstractRule<R, String> {
            private Pattern pattern;

            public Matches(Pattern pattern) {
                this.pattern = pattern;
            }

            @Override
            protected String getCode() {
                return "matches";
            }

            @Override
            protected boolean isValid(R rootObject, String value) {
                return value == null || pattern.matcher(value).matches();
            }
        }
    }

    interface Int {
        class GreaterOrEqual<R> extends AbstractRule<R, Integer> {
            private int value;

            public GreaterOrEqual(int value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "greaterOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value >= this.value;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class GreaterThan<R> extends AbstractRule<R, Integer> {
            private int value;

            public GreaterThan(int value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "greaterThan";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value > this.value;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class LessOrEqual<R> extends AbstractRule<R, Integer> {
            private int value;

            public LessOrEqual(int value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "lessOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value <= this.value;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class LessThan<R> extends AbstractRule<R, Integer> {
            private int value;

            public LessThan(int value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "lessThan";
            }

            @Override
            protected boolean isValid(R rootObject, Integer value) {
                return value != null && value < this.value;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }
    }

    interface N {
        class GreaterOrEqual<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V value;

            public GreaterOrEqual(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "greaterOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(this.value) >= 0;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class GreaterThan<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V value;

            public GreaterThan(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "greaterThan";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(this.value) > 0;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class LessOrEqual<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V value;

            public LessOrEqual(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "lessOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(this.value) <= 0;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }

        class LessThan<R, V extends Number & Comparable<V>> extends AbstractRule<R, V> {
            private V value;

            public LessThan(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "lessThan";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || value.compareTo(this.value) < 0;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", value);
            }
        }
    }

    interface D {
        class LessThan<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public LessThan(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "lessThan";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) < 0;
            }
        }

        class LessOrEqual<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public LessOrEqual(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "lessOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) <= 0;
            }
        }

        class GreaterThan<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public GreaterThan(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "greaterThan";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) > 0;
            }
        }

        class GreaterOrEqual<R, V> extends AbstractRule<R, V> {
            private Supplier<V> dateSupplier;
            private Comparator<V> comparator;

            public GreaterOrEqual(Supplier<V> dateSupplier, Comparator<V> comparator) {
                this.dateSupplier = dateSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "greaterOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value == null || comparator.compare(value, dateSupplier.get()) >= 0;
            }
        }

        class Between<R, V> extends AbstractRule<R, V> {
            private Supplier<V> afterSupplier;
            private Supplier<V> beforeSupplier;
            private Comparator<V> comparator;

            public Between(Supplier<V> afterSupplier, Supplier<V> beforeSupplier, Comparator<V> comparator) {
                this.afterSupplier = afterSupplier;
                this.beforeSupplier = beforeSupplier;
                this.comparator = comparator;
            }

            @Override
            protected String getCode() {
                return "between";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                if (value == null) {
                    return true;
                } else {
                    return comparator.compare(value, afterSupplier.get()) > 0 && comparator.compare(value, beforeSupplier.get()) < 0;
                }
            }
        }
    }

    interface Obj {
        class NotNull<R, V> extends AbstractRule<R, V> {
            @Override
            protected String getCode() {
                return "notNull";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value != null;
            }
        }
    }

    interface Coll {
        class NotEmpty<R, V> extends AbstractRule<R, Collection<V>> {

            @Override
            protected String getCode() {
                return "notEmpty";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value != null && !value.isEmpty();
            }
        }

        class GreaterOrEqual<R, V> extends AbstractRule<R, Collection<V>> {
            private int size;

            public GreaterOrEqual(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "greaterOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value != null && value.size() >= size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class GreaterThan<R, V> extends AbstractRule<R, Collection<V>> {
            private int size;

            public GreaterThan(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "greaterThan";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value != null && value.size() > size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class LessOrEqual<R, V> extends AbstractRule<R, Collection<V>> {
            private int size;

            public LessOrEqual(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "lessOrEqual";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value == null || value.size() <= size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class LessThan<R, V> extends AbstractRule<R, Collection<V>> {
            private int size;

            public LessThan(int size) {
                this.size = size;
            }

            @Override
            protected String getCode() {
                return "lessThan";
            }

            @Override
            protected boolean isValid(R rootObject, Collection<V> value) {
                return value == null || value.size() < size;
            }

            @Override
            public Map<String, Object> getErrorParams() {
                return FvUtils.to.map("criterion", size);
            }
        }

        class ItemValidator<R, V> implements FluentValidatorRule<R, Collection<V>> {

            private Fv.Validator<V> validator;

            public ItemValidator(Fv.Validator<V> validator) {
                this.validator = validator;
            }

            @Override
            public Fv.Result validate(R rootObject, String property, Collection<V> value) {
                if (value == null) {
                    return Fv.Result.success();
                } else {
                    Errors errors = Errors.ok();
                    int index = 0;

                    for (V item : value) {
                        Fv.Result result = validator.validate(item);

                        if (result.hasErrors()) {
                            errors.add(FluentValidatorObjects.ErrorUtils.addParentProperty(result.getErrors(), property + "[" + index + "]"));
                        }

                        index++;
                    }

                    if (errors.isOk()) {
                        return Fv.Result.success();
                    } else {
                        return Fv.Result.failure(rootObject, errors);
                    }
                }
            }
        }
    }

    interface Base {

        class Eq<R, V> extends AbstractRule<R, V> {

            private V value;

            public Eq(V value) {
                this.value = value;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return value != null && value.equals(this.value);
            }
        }

        class EqFunction<R, V> extends AbstractRule<R, V> {

            private Function<V, Boolean> eqFunction;

            public EqFunction(Function<V, Boolean> eqFunction) {
                this.eqFunction = eqFunction;
            }

            @Override
            protected String getCode() {
                return "notEq";
            }

            @Override
            protected boolean isValid(R rootObject, V value) {
                return eqFunction.apply(value);
            }
        }

        class NotEq<R, V> implements FluentValidatorRule<R, V> {

            private V value;

            public NotEq(V value) {
                this.value = value;
            }

            @Override
            public Fv.Result validate(R rootObject, String property, V value) {
                return null;
            }
        }

        class NotEqFunction<R, V> implements FluentValidatorRule<R, V> {

            private Function<V, Boolean> notEqFunction;

            public NotEqFunction(Function<V, Boolean> notEqFunction) {
                this.notEqFunction = notEqFunction;
            }

            @Override
            public Fv.Result validate(R rootObject, String property, V value) {
                return null;
            }
        }

        class Custom<R, V> implements FluentValidatorRule<R, V> {

            private FluentValidatorObjects.CustomValidator.Simple<R, V> customSimple;
            private FluentValidatorObjects.CustomValidator.WithBuilder<R, V> customWithBuilder;

            public Custom(FluentValidatorObjects.CustomValidator.Simple<R, V> customSimple) {
                this.customSimple = customSimple;
            }

            public Custom(FluentValidatorObjects.CustomValidator.WithBuilder<R, V> customWithBuilder) {
                this.customWithBuilder = customWithBuilder;
            }

            @Override
            public Fv.Result validate(R rootObject, String property, V value) {
                Fv.CustomResult result = null;

                if (customSimple != null) {
                    result = customSimple.validate(rootObject, value);
                } else if (customWithBuilder != null) {
                    result = customWithBuilder.validate(rootObject, value, new FluentValidatorBuilder<>());
                }

                if (result == null) {
                    return null;
                } else {
                    return toFvResult(result, rootObject, property, value);
                }
            }

            private Fv.Result toFvResult(Fv.CustomResult result, R rootObject, String property, V value) {
                if (result.isOk()) {
                    return Fv.Result.success(rootObject);
                } else {
                    Errors errorsSource = result.getErrors();
                    List<Err> errors = new ArrayList<>(errorsSource.get().size());

                    for (Err e : errorsSource) {
                        errors.add(Err
                                .field(FluentValidatorObjects.PropertyUtils.mix(property, e.getField()))
                                .code(e.getCode())
                                .value((e.hasField()) ? e.getValue() : value)
                                .params(e.getParams())
                                .build()
                        );
                    }

                    return Fv.Result.failure(rootObject, Errors.reject(errors));
                }
            }
        }

        class Validator<R, V> implements FluentValidatorRule<R, V> {

            private Fv.Validator<V> validator;

            public Validator(Fv.Validator<V> validator) {
                this.validator = validator;
            }

            @Override
            public Fv.Result validate(R rootObject, String property, V value) {
                Fv.Result result = validator.validate(value);

                if (result.isOk()) {
                    return result;
                } else {
                    return Fv.Result.failure(rootObject, FluentValidatorObjects.ErrorUtils.addParentProperty(result.getErrors(), property));
                }
            }
        }
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    class Error<V> {
        private String code;
        private V value;

        public Error(String code, V value) {
            this.code = code;
            this.value = value;
        }
    }
}
