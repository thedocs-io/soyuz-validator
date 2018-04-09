package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Test;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by fbelov on 05.04.18.
 */
public class SpringDependencyObjectValidationTest {

    private CompanyValidatorProvider companyValidatorProvider = SimpleSpringAkaContext.getInstance().getCompanyValidatorProvider();

    @Test
    public void shouldValidateCompanyNameNotEmpty() {
        //setup
        Company company = createValidCompany().toBuilder()
                .name("")
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Err.field("name").code("notEmpty").build());

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    private static class EmployeeValidatorProvider {

        private Fv.Validator<Employee> employeeValidator;

        public EmployeeValidatorProvider() {
            this.employeeValidator = Fv.of(Employee.class)
                    .primitiveInt("id").greaterThan(0).b()
                    .string("email").notEmpty().mail().b()
                    .string("name").notEmpty().b()
                    .integer("age").notNull().greaterOrEqual(18).b()
                    .build();
        }

        public Fv.Validator<Employee> get() {
            return employeeValidator;
        }
    }

    private static class CompanyValidatorProvider {

        private Fv.Validator<Company> companyValidator;

        public CompanyValidatorProvider(CompanyDao dao, EmployeeValidatorProvider employeeValidatorProvider) {
            this.companyValidator = Fv.of(Company.class)
                    .string("name").notEmpty().custom((o, v) -> {
                        if (is.t(v) && !dao.isNameUnique(v, o.getId())) {
                            return Fv.CustomResult.failure("notUnique");
                        } else {
                            return Fv.CustomResult.success();
                        }
                    }).b()
                    .collection("employees", Employee.class).notEmpty().itemValidator(employeeValidatorProvider.get()).custom((o, v) -> {
                        if (is.t(v) && dao.hasLinkedToOtherCompanies(v, o.getId())) {
                            return Fv.CustomResult.failure("hasLinkedToOtherAgencies");
                        } else {
                            return Fv.CustomResult.success();
                        }
                    }).b()
                    .object("address", Company.Address.class).customWithBuilder((o, value, validator) -> {
                        return Fv.CustomResult.from(
                                validator
                                        .string("city").notEmpty().b()
                                        .string("location").notEmpty().b()
                                        .build()
                                        .validate(value)
                        );
                    }).b()
                    .object("workingHours", Company.WorkingHours.class).validator(
                            Fv.of(Company.WorkingHours.class)
                                    .localTime("from").greaterOrEqual(LocalTime.of(0, 0)).lessOrEqual(LocalTime.of(23, 59, 59)).b()
                                    .localTime("to").greaterOrEqual(LocalTime.of(0, 0)).lessOrEqual(LocalTime.of(23, 59, 59)).b()
                                    .custom((o, v) -> {
                                        if (o.getFrom() != null && o.getTo() != null) {
                                            if (o.getFrom().compareTo(o.getTo()) >= 0) {
                                                return Fv.CustomResult.failure("fromShouldBeBeforeTo");
                                            }
                                        }

                                        return Fv.CustomResult.success();
                                    })
                                    .build()
                    )
                    .build();
        }

        public Fv.Validator<Company> get() {
            return companyValidator;
        }
    }

    private Company createValidCompany() {

    }

    @AllArgsConstructor
    private static class CompanyDao {

        private List<Company> fakeCompanies;

        public boolean isNameUnique(String name, Integer companyId) {
            Company company = fakeCompanies
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseGet(null);

            return company == null || Integer.valueOf(company.getId()).equals(companyId);
        }

        public boolean hasLinkedToOtherCompanies(Collection<Integer> employeeIds, Integer companyId) {
            Company company = fakeCompanies
                    .stream()
                    .filter(c -> c.getEmployeeIds().stream().anyMatch(employeeIds::contains))
                    .findFirst()
                    .orElseGet(null);

            return company == null || Integer.valueOf(company.getId()).equals(companyId);
        }
    }

    @Getter
    private static class SimpleSpringAkaContext {

        private static SimpleSpringAkaContext instance;

        private CompanyDao companyDao;
        private EmployeeValidatorProvider employeeValidatorProvider;
        private CompanyValidatorProvider companyValidatorProvider;

        private SimpleSpringAkaContext() {
            this.companyDao = new CompanyDao(to.list(

            ));
            this.employeeValidatorProvider = new EmployeeValidatorProvider();
            this.companyValidatorProvider = new CompanyValidatorProvider(companyDao, employeeValidatorProvider);
        }

        public static synchronized SimpleSpringAkaContext getInstance() {
            if (instance == null) {
                instance = new SimpleSpringAkaContext();
            }

            return instance;
        }
    }
}
