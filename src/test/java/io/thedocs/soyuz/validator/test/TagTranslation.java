package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.validator.FluentValidator;

/**
 * Created by fbelov on 06.05.16.
 */
class TagTranslation {

    private String lang;
    private String value;

    String getLang() {
        return lang;
    }

    String getValue() {
        return value;
    }

    public static FluentValidator<TagTranslation> getValidator() {
        return FluentValidator.of(TagTranslation.class)
                .string("lang").notEmpty().b()
                .string("value").notEmpty().b()
                .build();
    }
}
