package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.validator.Fv;

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

//    Fv tagTranslationValidator = validate(TagTranslation.class)
//            .string("title").notEmpty().build();
//
//    Fv v = validate(Fv.class)
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

    public static Fv.Validator<Actress> getValidator() {
        Object tagValidator = null;
//        Object tagTranslationChain = Fv.chain().object(TagTranslation.class).notNull().validator(tagTranslationValidator);
        TagTranslation jaTranslation = null;
        Fv.Validator<TagTranslation> tagTranslationValidator = TagTranslation.getValidator();

        return Fv.of(Actress.class)
                .failFast()
                .notNull()
                .customWithBuilder((object, propertyValue, fluentValidatorBuilder) -> {
                    return Fv.CustomResult.success();
                })
                .object("en", TagTranslation.class).validator(tagTranslationValidator).message("a.en.translation.wrong").b()
                .object("ja", TagTranslation.class).eq(jaTranslation).validator(tagTranslationValidator).b()
                .string("blogUrl").url().notEmpty().b()
                .collection("tags", Tag.class).notEmpty().greaterThan(0).lessThan(5).itemValidator(Tag.getValidator()).b()
                .object("sizes", Actress.Sizes.class).notNull().customWithBuilder((o, value, validator) -> {
                    return Fv.CustomResult.from(
                            validator
                                    .primitiveInt("height").greaterThan(4).lessThan(5).b()
                                    .primitiveInt("breast").greaterThan(1).b()
                                    .primitiveInt("waist").greaterThan(1).lessThan(999).b()
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
