package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.validator.Fv;

/**
 * Created by fbelov on 06.05.16.
 */
class Tag {

    private TagTranslation en;
    private TagTranslation ja;

    public static Fv.Validator<Tag> getValidator() {
        return Fv.of(Tag.class)
                .object("en", TagTranslation.class).notNull().validator(TagTranslation.getValidator()).b()
                .object("ja", TagTranslation.class).notNull().validator(TagTranslation.getValidator())
                .when((o, v) -> {
                    return o != null;
                })
                .b()
                .build();
    }

}
