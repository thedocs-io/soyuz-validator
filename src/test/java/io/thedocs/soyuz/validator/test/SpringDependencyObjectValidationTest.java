package io.thedocs.soyuz.validator.test;

import io.thedocs.soyuz.is;
import io.thedocs.soyuz.validator.FluentValidator;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Created by fbelov on 05.04.18.
 */
public class SpringDependencyObjectValidationTest {

    private CompanyValidatorProvider companyValidatorProvider;

    public SpringDependencyObjectValidationTest() {

    }

    private static class CompanyValidatorProvider {

        private FluentValidator<Company> companyValidator;

        public CompanyValidatorProvider(CompanyDao dao) {
            this.companyValidator = FluentValidator.of(Company.class)
                    .string("name").notEmpty().custom((o, v) -> {
                        if (is.t(v) && !dao.isNameUnique(v, o.getId())) {
                            return FluentValidator.CustomResult.failure("notUnique");
                        } else {
                            return FluentValidator.CustomResult.success();
                        }
                    }).b()
                    .collection("employeeIds", Integer.class).custom((o, v) -> {
                        if (is.t(v) && dao.hasLinkedToOtherCompanies(v, o.getId())) {
                            return FluentValidator.CustomResult.failure("hasLinkedToOtherAgencies");
                        } else {
                            return FluentValidator.CustomResult.success();
                        }
                    }).b()
                    .build();
        }

        public FluentValidator<Company> get() {
            return companyValidator;
        }
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
}
