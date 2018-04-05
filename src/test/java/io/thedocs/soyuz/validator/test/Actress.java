package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.validator.FluentValidator;

import java.util.Set;

/**
 * Created by fbelov on 06.05.16.
 */
public class Actress {

    private TagTranslation en;
    private TagTranslation ja;
    private String blogUrl;
    private Set<Tag> tags;
    private Sizes sizes;

//    FluentValidator tagTranslationValidator = validate(TagTranslation.class)
//            .string("title").notEmpty().build();
//
//    FluentValidator v = validate(FluentValidator.class)
//            .integer("name").notNull().isGreaterThan(20).back()
//            .string("hello").custom((a) -> {
//
//    }).back()
//            .custom((o, context) -> {
//        Validator(context)
//                .string("property", o.getName()).notEmpty().back()
//                .integer("another", o.getAge()).isGreaterThan(20).back()
//                .object("trololo", o.getObject()).validator(vv)
//                .validate();
//    })
//            .list("tagTranslation").validator(tagTranslationValidator).back()
//            .build();

    public static FluentValidator<Actress> getValidator() {
        Object tagValidator = null;
//        Object tagTranslationChain = FluentValidator.chain().object(TagTranslation.class).notNull().validator(tagTranslationValidator);
        TagTranslation jaTranslation = null;
        FluentValidator<TagTranslation> tagTranslationValidator = TagTranslation.getValidator();

        return FluentValidator.of(Actress.class)
                .failFast()
                .notNull()
                .custom((object, propertyValue, fluentValidatorBuilder) -> {
                    return FluentValidator.CustomResult.success();
                })
                .object("en", TagTranslation.class).validator(tagTranslationValidator).message("a.en.translation.wrong").b()
                .object("ja", TagTranslation.class).eq(jaTranslation).validator(tagTranslationValidator).b()
                .string("blogUrl").url().notEmpty().b()
                .collection("tags", Tag.class).notEmpty().greaterThan(0).lessThan(5).itemValidator(Tag.getValidator()).b()
                .object("sizes", Actress.Sizes.class).notNull().custom((o, value, validator) -> {
                    return FluentValidator.CustomResult.from(
                            validator
                                    .i("height").greaterThan(4).lessThan(5).b()
                                    .i("breast").greaterThan(1).b()
                                    .i("waist").greaterThan(1).lessThan(999).b()
                                    .build()
                                    .validate(value)
                    );
                }).b()
                .build();
    }

    public static class Sizes {
        private int height;
        private int breast;
        private int waist;
        private int hip;
    }
}
