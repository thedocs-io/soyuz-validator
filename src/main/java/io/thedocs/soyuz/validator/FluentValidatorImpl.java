package io.thedocs.soyuz.validator;

import io.thedocs.soyuz.err.Errors;
import lombok.ToString;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fbelov on 05.04.18.
 */
@ToString
public class FluentValidatorImpl<R> implements Fv.Validator<R> {
    private static final PropertyUtilsBean PROPERTY_UTILS_BEAN = BeanUtilsBean.getInstance().getPropertyUtils();

    private List<FluentValidatorBuilder.ValidationDataWithProperties> validationData = new ArrayList<>();

    public FluentValidatorImpl(List<FluentValidatorBuilder.ValidationDataWithProperties> validationData) {
        this.validationData = validationData;
    }

    public List<FluentValidatorBuilder.ValidationDataWithProperties> getValidationData() {
        return validationData;
    }

    public Fv.Result<R> validate(R rootObject) {
        Errors errors = Errors.ok();

        for (FluentValidatorBuilder.ValidationDataWithProperties validationDataWithProperties : validationData) {
            String property = validationDataWithProperties.getProperty();
            Object value = getPropertyValue(rootObject, property);

            Fv.Result result = validationDataWithProperties.getData().validate(rootObject, property, value);

            if (result != null) {
                errors.add(result.getErrors());
            }
        }

        return Fv.Result.failure(rootObject, errors);
    }

    private Object getPropertyValue(R o, String property) {
        try {
            if (o == null) {
                return null;
            } else if (property == null) {
                return o;
            } else {
                return PROPERTY_UTILS_BEAN.getNestedProperty(o, property);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
