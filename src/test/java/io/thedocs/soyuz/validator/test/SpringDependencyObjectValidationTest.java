package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.err.Err;
import io.thedocs.soyuz.err.Errors;
import io.thedocs.soyuz.is;
import io.thedocs.soyuz.to;
import io.thedocs.soyuz.validator.Fv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.junit.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This test demonstrates complex cases:
 *  - dependency injection - e.g. you need to check that email is unique. In this case you need to access DB - inject Service / DAO into your validator.
 *  - custom validation logic
 *  - validation collection of items
 */
public class SpringDependencyObjectValidationTest {

    /**
     * Let's define our model first. We've got Company object with List of Employee objects, complex Address and WorkingHours
     */
    @Getter
    @AllArgsConstructor
    @Builder(toBuilder = true)
    @ToString
    public static class Company {
        private int id;
        private String name;
        private List<Employee> employees;
        private Address address;
        private WorkingHours workingHours;

        @AllArgsConstructor
        @Getter
        @Builder(toBuilder = true)
        @ToString
        public static class Address {
            private String city;
            private String location;
        }

        @AllArgsConstructor
        @Getter
        @Builder(toBuilder = true)
        @ToString
        public static class WorkingHours {
            private LocalTime from;
            private LocalTime to;
        }
    }

    /**
     * Pretty simple Employee object
     */
    @AllArgsConstructor
    @Getter
    @Builder
    @ToString
    public static class Employee {
        private int id;
        private String email;
        private String name;
        private Integer age;
    }

    /**
     * Our fake DAO to check that company name is unique
     */
    @AllArgsConstructor
    private static class CompanyDao {

        private List<Company> fakeCompanies;

        public boolean isNameUnique(String name, Integer companyId) {
            Company company = fakeCompanies
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);

            return company == null || Integer.valueOf(company.getId()).equals(companyId);
        }
    }


    /**
     * Our fake Spring context. We inject companyDao into companyValidator to be able to check that company name is unique
     */
    @Getter
    private static class SimpleSpringAkaContext {

        private static SimpleSpringAkaContext instance;

        private CompanyDao companyDao;
        private EmployeeValidatorProvider employeeValidatorProvider;
        private CompanyValidatorProvider companyValidatorProvider;

        private SimpleSpringAkaContext() {
            this.companyDao = new CompanyDao(to.list(
                    Company.builder()
                            .id(2)
                            .name("Gazprom")
                            .address(new Company.Address("Saint Petersburg", "Street"))
                            .employees(to.list())
                            .build()
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

    //
    // Finally let's define our validators. We wrapped it with special class to be able to inject it without any Spring Qualifier
    //

    /**
     * Employee has simple properties. So its validator is simple
     */
    private static class EmployeeValidatorProvider {

        private Fv.Validator<Employee> employeeValidator;

        public EmployeeValidatorProvider() {
            this.employeeValidator = Fv.of(Employee.class)
                    .primitiveInt("id").greaterThan(0).b()
                    .string("email").notEmpty().email().b()
                    .string("name").notEmpty().b()
                    .integer("age").notNull().greaterOrEqual(18).b()
                    .build();
        }

        public Fv.Validator<Employee> get() {
            return employeeValidator;
        }
    }

    /**
     * !!! And here is our complex validator ===>>>
     */
    private static class CompanyValidatorProvider {

        private Fv.Validator<Company> companyValidator;

        public CompanyValidatorProvider(CompanyDao dao, EmployeeValidatorProvider employeeValidatorProvider) {
            this.companyValidator = Fv.of(Company.class)
                    .string("name").notEmpty().custom((o, v) -> { //we use custom function to define our custom logic to check that name company is unique
                        if (is.t(v) && !dao.isNameUnique(v, o.getId())) {
                            return Fv.CustomResult.failure("notUnique");
                        } else {
                            return Fv.CustomResult.success();
                        }
                    }).b()
                    .collection("employees", Employee.class).notEmpty().itemValidator(employeeValidatorProvider.get()).b() //validate collection of Employees. Each item is validated by employeeValidator
                    .object("workingHours", Company.WorkingHours.class).validator( //we can create Validator on the fly
                            Fv.of(Company.WorkingHours.class) // [02:00:00 - 23:59:59], from should be before to
                                    .localTime("from").greaterOrEqual(LocalTime.of(2, 0)).lessOrEqual(LocalTime.of(23, 59, 59)).b()
                                    .localTime("to").greaterOrEqual(LocalTime.of(2, 0)).lessOrEqual(LocalTime.of(23, 59, 59)).b()
                                    .custom((o, v) -> {
                                        if (o.getFrom() != null && o.getTo() != null) {
                                            if (o.getFrom().compareTo(o.getTo()) >= 0) {
                                                return Fv.CustomResult.failure("fromShouldBeBeforeTo");
                                            }
                                        }

                                        return Fv.CustomResult.success();
                                    })
                                    .build()
                    ).b()
                    .object("address", Company.Address.class).customWithBuilder((o, value, validator) -> { //validate custom object with inline validator
                        return Fv.CustomResult.from(
                                validator
                                        .string("city").notEmpty().b()
                                        .string("location").notEmpty().b()
                                        .build()
                                        .validate(value)
                        );
                    }).b()
                    .build();
        }

        public Fv.Validator<Company> get() {
            return companyValidator;
        }
    }

    //
    // And finally let's check that our validators really works
    //

    private CompanyValidatorProvider companyValidatorProvider = SimpleSpringAkaContext.getInstance().getCompanyValidatorProvider();

    @Test
    public void shouldValidateCompanyNameNotEmpty_simplePropertyConstraint() {
        //setup
        Company company = createValidCompany().toBuilder()
                .name("")
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Err.field("name").code("notEmpty").value("").build());

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    @Test
    public void shouldValidateCompanyNameIsUnique_dependencyInjection() {
        //setup
        Company company = createValidCompany().toBuilder()
                .name("Gazprom")
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Err.field("name").code("notUnique").value("Gazprom").build());

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    @Test
    public void shouldValidateAddress_subObjectValidationWithBuilder() {
        //setup
        Company company = createValidCompany().toBuilder()
                .address(new Company.Address("", ""))
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Errors.reject(
                Err.field("address.city").code("notEmpty").value("").build(),
                Err.field("address.location").code("notEmpty").value("").build()
        ));

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    @Test
    public void shouldValidateWorkingHours_subObjectValidator() {
        //setup
        Company company = createValidCompany().toBuilder()
                .workingHours(new Company.WorkingHours(LocalTime.of(1, 55), LocalTime.of(1, 30)))
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Errors.reject(
                Err.field("workingHours.from").code("greaterOrEqual").value(LocalTime.of(1, 55)).build(),
                Err.field("workingHours.to").code("greaterOrEqual").value(LocalTime.of(1, 30)).build(),
                Err.field("workingHours").code("fromShouldBeBeforeTo").value(company.getWorkingHours()).build()
        ));

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    @Test
    public void shouldValidateEmployees_itemValidator() {
        //setup
        Company company = createValidCompany().toBuilder()
                .employees(to.list(
                        new Employee(-1, "pupkin", "Fedor", 10),
                        new Employee(1, "lopatkin@gmail.com", "", null)
                ))
                .build();

        Fv.Result<Company> resultExpected = Fv.Result.failure(company, Errors.reject(
                Err.field("employees[0].id").code("greaterThan").value(-1).params(to.map("criterion", 0)).build(),
                Err.field("employees[0].email").code("email").value("pupkin").build(),
                Err.field("employees[0].age").code("greaterOrEqual").value(10).params(to.map("criterion", 18)).build(),
                Err.field("employees[1].name").code("notEmpty").value("").build(),
                Err.field("employees[1].age").code("notNull").value(null).build()
        ));

        //when
        Fv.Result<Company> result = companyValidatorProvider.get().validate(company);

        //then
        assertEquals(resultExpected, result);
    }

    private Company createValidCompany() {
        return Company.builder()
                .id(1)
                .name("Averta")
                .employees(to.list(
                        Employee.builder().id(1).name("fedor").age(30).email("pupkin@gmail.com").build(),
                        Employee.builder().id(2).name("Vasiliy").age(22).email("lopatkin@gmail.com").build()
                ))
                .address(new Company.Address("Moscow", "Trevskaya street, 5"))
                .workingHours(new Company.WorkingHours(LocalTime.of(3, 0), LocalTime.of(23, 55)))
                .build();
    }

}
