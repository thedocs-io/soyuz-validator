package io.thedocs.soyuz.validator;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Created by fbelov on 22.05.16.
 */
public class FluentValidatorObjects {

    public static class BaseBuilder<R, V, BuilderClass, DataClass extends BaseData<R, V>> {

        protected DataClass data;

        public BaseBuilder(DataClass data) {
            this.data = data;
        }

        public BuilderClass eq(V value) {
            data.addRule(new FluentValidatorRule.Base.Eq<>(value));

            return _this();
        }

        public BuilderClass eq(Function<V, Boolean> eqFunction) {
            data.addRule(new FluentValidatorRule.Base.EqFunction<>(eqFunction));

            return _this();
        }

        public BuilderClass notEq(V value) {
            data.addRule(new FluentValidatorRule.Base.NotEq<>(value));

            return _this();
        }

        public BuilderClass notEq(Function<V, Boolean> notEqFunction) {
            data.addRule(new FluentValidatorRule.Base.NotEqFunction<>(notEqFunction));

            return _this();
        }

        public BuilderClass when(BiFunction<R, V, Boolean> when) {
            data.addWhen(when);

            return _this();
        }

        public BuilderClass when(Function<R, Boolean> when) {
            data.addWhen(when);

            return _this();
        }

        public BuilderClass unless(BiFunction<R, V, Boolean> unless) {
            data.setUnless(unless);

            return _this();
        }


        public BuilderClass validator(Fv.Validator<V> validator) {
            data.addRule(new FluentValidatorRule.Base.Validator<>(validator));

            return _this();
        }

        public BuilderClass custom(CustomValidator.Simple<R, V> fluentValidatorCustom) {
            data.addRule(new FluentValidatorRule.Base.Custom<>(fluentValidatorCustom));

            return _this();
        }

        public BuilderClass customWithBuilder(CustomValidator.WithBuilder<R, V> customValidatorWithBuilder) {
            data.addRule(new FluentValidatorRule.Base.Custom<>(customValidatorWithBuilder));

            return _this();
        }

        public BuilderClass message(String message) {
            data.setMessage(message);

            return _this();
        }

        protected BuilderClass _this() {
            return (BuilderClass) this;
        }
    }

    /**
     * Created by fbelov on 22.05.16.
     */
    public interface CustomValidator {

        interface Simple<P, V> extends CustomValidator {
            Fv.CustomResult validate(P object, V propertyValue);
        }

        interface WithBuilder<P, V> extends CustomValidator {
            Fv.CustomResult validate(P object, V propertyValue, FluentValidatorBuilder<V> fluentValidatorBuilder);
        }
    }

    /**
     * Created by fbelov on 22.05.16.
     */
    public interface FluentValidatorValidationData<R, V> {

        Fv.Result validate(R rootObject, String property, V value);

    }

    @Getter
    @Setter
    public static class BaseData<R, V> implements FluentValidatorValidationData<R, V> {

        private List<FluentValidatorRule<R, V>> rules = new ArrayList<>();

//        private V eq;
//        private Function<V, Boolean> eqFunction;
//        private V notEq;
//        private Function<V, Boolean> notEqFunction;
        private List<BiFunction> when = new ArrayList<>();
        private BiFunction unless;
        private Fv.Validator<V> validator;
        private final List<CustomValidator> customValidators = new ArrayList<>();
        private String message;

        public void addWhen(BiFunction<?, V, Boolean> when) {
            this.when.add(when);
        }

        public <P> void addWhen(Function<P, Boolean> when) {
            this.when.add((p, v) -> when.apply((P) p));
        }

        public void addRule(FluentValidatorRule<R, V> rule) {
            rules.add(rule);
        }

        public void addCustom(CustomValidator FluentValidatorCustom) {
            customValidators.add(FluentValidatorCustom);
        }

        @Override
        public Fv.Result validate(R rootObject, String property, V value) {
            for (BiFunction<R, V, Boolean> whenItem : when) {
                if (!whenItem.apply(rootObject, value)) {
                    return null;
                }
            }

            //todo unless

            for (FluentValidatorRule<R, V> rule : rules) {
                Fv.Result result = rule.validate(rootObject, property, value);

                if (result != null && result.hasErrors()) {
                    return result;
                }
            }

            return null;
        }
    }

    @Data
    public static class RootData<R, V> extends ObjectData<R, V> {
        private boolean failFast;
    }

    @Data
    public static class ObjectData<R, V> extends BaseData<R, V> {
        private boolean notNull;


        public ObjectData notNull() {
            notNull = true;
            return this;
        }
    }

    public static class IntData<R> extends BaseData<R, Integer> {

    }

    public static class NumberData<R, V extends Number & Comparable<V>> extends BaseData<R, V> {

    }

    @Setter
    @Getter
    public static class StringData<R> extends ObjectData<R, String> {
        private Pattern matches;
    }

    public static class DateData<R> extends ObjectData<R, Date> {

    }

    public static class LocalDateData<R> extends ObjectData<R, LocalDate> {

    }

    public static class LocalTimeData<R> extends ObjectData<R, LocalTime> {

    }

    public static class CollectionData<R, V> extends ObjectData<R, V> {

    }

    public static class PropertyUtils {

        public static String mix(String parent, String child) {
            if (parent == null) {
                return child;
            } else if (child == null) {
                return parent;
            } else {
                return parent + "." + child;
            }
        }

    }

    public static class ErrorUtils {

        public static Errors addParentProperty(Errors errors, String parentProperty) {
            List<Err> answer = new ArrayList<>(errors.get().size());

            for (Err error : errors) {
                answer.add(addParentProperty(error, parentProperty));
            }

            return Errors.reject(answer);
        }

        public static Err addParentProperty(Err error, String parentProperty) {
            return error.toBuilder().field(PropertyUtils.mix(parentProperty, error.getField())).build();
        }

    }
}
