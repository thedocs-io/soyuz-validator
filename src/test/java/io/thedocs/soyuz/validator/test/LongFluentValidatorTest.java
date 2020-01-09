package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.to;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.function.Function;

public class LongFluentValidatorTest {

    private boolean assertEquals(Car car, Fv.Validator<Car> validator, Function<Car, Fv.Result<Car>> result) {
        org.junit.Assert.assertEquals(validator.validate(car), result.apply(car));

        return true;
    }

    @Test
    public void greaterThan() {
        //when
        Fv.Validator<Car> validator = Fv.of(Car.class).long_("power").greaterThan(50L).b().build();

        //then
        assertEquals(new Car(), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(150L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(50L), validator, c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(50L).params(to.map("criterion", 50L)).build()));
        assertEquals(new Car(10L), validator, c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(10L).params(to.map("criterion", 50L)).build()));
    }

    @Test
    public void greaterOrEqual() {
        //when
        Fv.Validator<Car> validator = Fv.of(Car.class).long_("power").greaterOrEqual(50L).b().build();

        //then
        assertEquals(new Car(), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(150L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(50L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(10L), validator, c -> Fv.Result.failure(c, Err.field("power").code("greaterOrEqual").value(10L).params(to.map("criterion", 50L)).build()));
    }

    @Test
    public void lessThan() {
        //when
        Fv.Validator<Car> validator = Fv.of(Car.class).long_("power").lessThan(999L).b().build();

        //then
        assertEquals(new Car(), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(150L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(999L), validator, c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(999L).params(to.map("criterion", 999L)).build()));
        assertEquals(new Car(1500L), validator, c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(1500L).params(to.map("criterion", 999L)).build()));
    }

    @Test
    public void lessOrEqual() {
        //when
        Fv.Validator<Car> validator = Fv.of(Car.class).long_("power").lessOrEqual(999L).b().build();

        //then
        assertEquals(new Car(), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(150L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(999L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(1500L), validator, c -> Fv.Result.failure(c, Err.field("power").code("lessOrEqual").value(1500L).params(to.map("criterion", 999L)).build()));
    }

    @Test
    public void greaterThan_plus_lessThan() {
        //when
        Fv.Validator<Car> validator = Fv.of(Car.class).long_("power").greaterThan(1L).lessThan(999L).b().build();

        //then
        assertEquals(new Car(), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(150L), validator, c -> Fv.Result.success(c));
        assertEquals(new Car(1500L), validator, c -> Fv.Result.failure(c, Err.field("power").code("lessThan").value(1500L).params(to.map("criterion", 999L)).build()));
        assertEquals(new Car(-1L), validator, c -> Fv.Result.failure(c, Err.field("power").code("greaterThan").value(-1L).params(to.map("criterion", 1L)).build()));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Car {
        private Long power;
    }
}
