package io.thedocs.soyuz.validator;

import lombok.ToString;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 5/10/16.
 */
public class FluentValidatorBuilder<T> extends FluentValidatorObjects.BaseBuilder<T, T, FluentValidatorBuilder<T>, FluentValidatorObjects.RootData<T, T>> {

    private String rootProperty;
    private List<ValidationDataWithProperties> validationData = new ArrayList<>();

    public FluentValidatorBuilder() {
        this(null);
    }

    public FluentValidatorBuilder(String rootProperty) {
        super(new FluentValidatorObjects.RootData<T, T>());

        this.rootProperty = rootProperty;
    }

    public FluentValidatorBuilder<T> failFast() {
        return failFast(true);
    }

    public FluentValidatorBuilder<T> failFast(boolean failFast) {
        data.setFailFast(failFast);
        return this;
    }

    public FluentValidatorBuilder<T> notNull() {
        data.notNull();
        return this;
    }

    public ObjectBuilder<T, Object> object(String property) {
        return new ObjectBuilder<>(this, getFullProperty(property));
    }

    public <V> ObjectBuilder<T, V> object(String property, Class<V> clazz) {
        return new ObjectBuilder<>(this, getFullProperty(property));
    }

    public StringBuilder<T> string(String property) {
        return new StringBuilder<>(this, getFullProperty(property));
    }

    public DateBuilder<T> date(String property) {
        return new DateBuilder<>(this, getFullProperty(property));
    }

    public LocalDateBuilder<T> localDate(String property) {
        return new LocalDateBuilder<>(this, getFullProperty(property));
    }

    public LocalTimeBuilder<T> localTime(String property) {
        return new LocalTimeBuilder<>(this, getFullProperty(property));
    }

    public CollectionBuilder<T, Object> collection(String property) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public <V> CollectionBuilder<T, V> collection(String property, Class<V> clazz) {
        return new CollectionBuilder<>(this, getFullProperty(property));
    }

    public PrimitiveIntBuilder<T> primitiveInt(String property) {
        return new PrimitiveIntBuilder<>(this, getFullProperty(property));
    }

    public IntegerBuilder<T> integer(String property) {
        return new IntegerBuilder<>(this, getFullProperty(property));
    }

    public DoubleBuilder<T> double_(String property) {
        return new DoubleBuilder<>(this, getFullProperty(property));
    }

    public LongBuilder<T> long_(String property) {
        return new LongBuilder<>(this, getFullProperty(property));
    }

    public Fv.Validator<T> build() {
        validationData.add(new ValidationDataWithProperties(null, data));

        return new FluentValidatorImpl<T>(validationData);
    }

    private FluentValidatorBuilder<T> addFluentValidatorValidationData(String property, FluentValidatorObjects.FluentValidatorValidationData validationData) {
        this.validationData.add(new ValidationDataWithProperties(property, validationData));

        return this;
    }

    private String getFullProperty(String property) {
        if (rootProperty == null) {
            return property;
        } else {
            return rootProperty + "." + property;
        }
    }

    public static class PrimitiveIntBuilder<R> extends AbstractBuilder<R, Integer, PrimitiveIntBuilder<R>, FluentValidatorObjects.IntData<R>> {

        public PrimitiveIntBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.IntData(), property);
        }

        public PrimitiveIntBuilder<R> greaterOrEqual(int value) {
            data.addRule(new FluentValidatorRule.Int.GreaterOrEqual<>(value));
            return this;
        }

        public PrimitiveIntBuilder<R> greaterThan(int value) {
            data.addRule(new FluentValidatorRule.Int.GreaterThan<>(value));
            return this;
        }

        public PrimitiveIntBuilder<R> lessOrEqual(int value) {
            data.addRule(new FluentValidatorRule.Int.LessOrEqual<>(value));
            return this;
        }

        public PrimitiveIntBuilder<R> lessThan(int value) {
            data.addRule(new FluentValidatorRule.Int.LessThan<>(value));
            return this;
        }
    }

    public static class IntegerBuilder<R> extends AbstractNumberBuilder<R, Integer, IntegerBuilder<R>, FluentValidatorObjects.NumberData<R, Integer>> {
        public IntegerBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.NumberData<>(), property);
        }
    }

    public static class LongBuilder<R> extends AbstractNumberBuilder<R, Long, IntegerBuilder<R>, FluentValidatorObjects.NumberData<R, Long>> {
        public LongBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.NumberData<>(), property);
        }
    }

    public static class DoubleBuilder<R> extends AbstractNumberBuilder<R, Double, DoubleBuilder<R>, FluentValidatorObjects.NumberData<R, Double>> {
        public DoubleBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.NumberData<>(), property);
        }
    }

    public static class StringBuilder<R> extends AbstractObjectBuilder<R, String, StringBuilder<R>, FluentValidatorObjects.StringData<R>> {

        public StringBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.StringData<>(), property);
        }

        public StringBuilder<R> url() {
            data.addRule(new FluentValidatorRule.Str.Url<>());

            return this;
        }

        public StringBuilder<R> email() {
            data.addRule(new FluentValidatorRule.Str.Email<>());

            return this;
        }

        public StringBuilder<R> notEmpty() {
            data.addRule(new FluentValidatorRule.Str.NotEmpty());

            return this;
        }

        public StringBuilder<R> isBoolean() {
            data.addRule(new FluentValidatorRule.Str.IsBoolean<>());

            return this;
        }

        public StringBuilder<R> isByte() {
            data.addRule(new FluentValidatorRule.Str.IsByte<>());

            return this;
        }

        public StringBuilder<R> isShort() {
            data.addRule(new FluentValidatorRule.Str.IsShort<>());

            return this;
        }

        public StringBuilder<R> isInteger() {
            data.addRule(new FluentValidatorRule.Str.IsInteger<>());

            return this;
        }

        public StringBuilder<R> isLong() {
            data.addRule(new FluentValidatorRule.Str.IsLong<>());

            return this;
        }

        public StringBuilder<R> isFloat() {
            data.addRule(new FluentValidatorRule.Str.IsFloat<>());

            return this;
        }

        public StringBuilder<R> isDouble() {
            data.addRule(new FluentValidatorRule.Str.IsDouble<>());

            return this;
        }

        public StringBuilder<R> greaterOrEqual(int size) {
            data.addRule(new FluentValidatorRule.Str.GreaterOrEqual<>(size));

            return this;
        }

        public StringBuilder<R> greaterThan(int size) {
            data.addRule(new FluentValidatorRule.Str.GreaterThan<>(size));

            return this;
        }

        public StringBuilder<R> lessOrEqual(int size) {
            data.addRule(new FluentValidatorRule.Str.LessOrEqual<>(size));

            return this;
        }

        public StringBuilder<R> lessThan(int size) {
            data.addRule(new FluentValidatorRule.Str.LessThan<>(size));

            return this;
        }

        public StringBuilder<R> matches(Pattern pattern) {
            data.addRule(new FluentValidatorRule.Str.Matches<>(pattern));

            return this;
        }
    }


    public static class DateBuilder<R> extends AbstractDateBuilder<R, Date, DateBuilder<R>, FluentValidatorObjects.DateData<R>> {
        public DateBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.DateData<>(), property, getComparableComparator());
        }
    }

    public static class LocalDateBuilder<R> extends AbstractDateBuilder<R, LocalDate, LocalDateBuilder<R>, FluentValidatorObjects.LocalDateData<R>> {
        public LocalDateBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.LocalDateData<>(), property, getComparableComparator());
        }
    }

    public static class LocalTimeBuilder<R> extends AbstractDateBuilder<R, LocalTime, LocalTimeBuilder<R>, FluentValidatorObjects.LocalTimeData<R>> {
        public LocalTimeBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.LocalTimeData<>(), property, getComparableComparator());
        }
    }

    public static abstract class AbstractDateBuilder<R, V, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends AbstractObjectBuilder<R, V, BuilderClass, DataClass> {

        private Comparator<V> comparator;

        public AbstractDateBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property, Comparator<V> comparator) {
            super(builder, data, property);

            this.comparator = comparator;
        }

        public BuilderClass lessThan(V date) {
            return lessThan(() -> date);
        }

        public BuilderClass lessThan(Supplier<V> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.LessThan<>(dateSupplier, comparator));

            return _this();
        }

        public BuilderClass lessOrEqual(V date) {
            return lessOrEqual(() -> date);
        }

        public BuilderClass lessOrEqual(Supplier<V> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.LessOrEqual<>(dateSupplier, comparator));

            return _this();
        }

        public BuilderClass greaterThan(V date) {
            return greaterThan(() -> date);
        }

        public BuilderClass greaterThan(Supplier<V> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.GreaterThan<>(dateSupplier, comparator));

            return _this();
        }

        public BuilderClass greaterOrEqual(V date) {
            return greaterOrEqual(() -> date);
        }

        public BuilderClass greaterOrEqual(Supplier<V> dateSupplier) {
            data.addRule(new FluentValidatorRule.D.GreaterOrEqual<>(dateSupplier, comparator));

            return _this();
        }

        public BuilderClass between(V after, V before) {
            return between(() -> after, () -> before);
        }

        public BuilderClass between(Supplier<V> afterSupplier, Supplier<V> beforeSupplier) {
            data.addRule(new FluentValidatorRule.D.Between<>(afterSupplier, beforeSupplier, comparator));

            return _this();
        }
    }

    public static class CollectionBuilder<R, V> extends AbstractObjectBuilder<R, Collection<V>, CollectionBuilder<R, V>, FluentValidatorObjects.CollectionData<R, Collection<V>>> {

        public CollectionBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.CollectionData<>(), property);
        }

        public CollectionBuilder<R, V> notEmpty() {
            data.addRule(new FluentValidatorRule.Coll.NotEmpty<>());

            return this;
        }

        public CollectionBuilder<R, V> greaterOrEqual(int size) {
            data.addRule(new FluentValidatorRule.Coll.GreaterOrEqual<>(size));

            return this;
        }

        public CollectionBuilder<R, V> greaterThan(int size) {
            data.addRule(new FluentValidatorRule.Coll.GreaterThan<>(size));

            return this;
        }

        public CollectionBuilder<R, V> lessOrEqual(int size) {
            data.addRule(new FluentValidatorRule.Coll.LessOrEqual<>(size));

            return this;
        }

        public CollectionBuilder<R, V> lessThan(int size) {
            data.addRule(new FluentValidatorRule.Coll.LessThan<>(size));

            return this;
        }

        public CollectionBuilder<R, V> itemValidator(Fv.Validator<V> validator) {
            data.addRule(new FluentValidatorRule.Coll.ItemValidator<>(validator));

            return _this();
        }
    }

    public static class ObjectBuilder<R, V> extends AbstractObjectBuilder<R, V, ObjectBuilder<R, V>, FluentValidatorObjects.ObjectData<R, V>> {
        public ObjectBuilder(FluentValidatorBuilder<R> builder, String property) {
            super(builder, new FluentValidatorObjects.ObjectData<>(), property);
        }
    }

    private static class AbstractNumberBuilder<R, V extends Number & Comparable<V>, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends AbstractObjectBuilder<R, V, BuilderClass, DataClass> {

        public AbstractNumberBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(builder, data, property);
        }

        public BuilderClass greaterOrEqual(V value) {
            data.addRule(new FluentValidatorRule.N.GreaterOrEqual<>(value));

            return _this();
        }

        public BuilderClass greaterThan(V value) {
            data.addRule(new FluentValidatorRule.N.GreaterThan<>(value));

            return _this();
        }

        public BuilderClass lessOrEqual(V value) {
            data.addRule(new FluentValidatorRule.N.LessOrEqual<>(value));

            return _this();
        }

        public BuilderClass lessThan(V value) {
            data.addRule(new FluentValidatorRule.N.LessThan<>(value));

            return _this();
        }
    }

    private static abstract class AbstractObjectBuilder<R, V, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends AbstractBuilder<R, V, BuilderClass, DataClass> {
        public AbstractObjectBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(builder, data, property);
        }

        public BuilderClass notNull() {
            data.addRule(new FluentValidatorRule.Obj.NotNull<R, V>());

            return _this();
        }
    }

    private static abstract class AbstractBuilder<R, V, BuilderClass, DataClass extends FluentValidatorObjects.BaseData<R, V>> extends FluentValidatorObjects.BaseBuilder<R, V, BuilderClass, DataClass> {

        protected FluentValidatorBuilder<R> builder;
        protected String property;

        public AbstractBuilder(FluentValidatorBuilder<R> builder, DataClass data, String property) {
            super(data);
            this.builder = builder;
            this.property = property;
        }

        public FluentValidatorBuilder<R> b() {
            return builder.addFluentValidatorValidationData(property, data);
        }

    }

    private static <V extends Comparable> Comparator<V> getComparableComparator() {
        return (o1, o2) -> {
            if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            } else {
                return o1.compareTo(o2);
            }
        };
    }

    @ToString
    public static class ValidationDataWithProperties {
        private String property;
        private FluentValidatorObjects.FluentValidatorValidationData data;

        public ValidationDataWithProperties(String property, FluentValidatorObjects.FluentValidatorValidationData data) {
            this.property = property;
            this.data = data;
        }

        @Nullable
        public String getProperty() {
            return property;
        }

        public FluentValidatorObjects.FluentValidatorValidationData getData() {
            return data;
        }
    }
}
