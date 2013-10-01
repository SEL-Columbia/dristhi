package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Dates;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.ei.drishti.reporting.domain.Dates.FIND_DATES_BY_DATE;

@Repository
public class AllDatesRepository implements DatesCacheableRepository {
    private DataAccessTemplate template;

    protected AllDatesRepository() {
    }

    public AllDatesRepository(DataAccessTemplate dataAccessTemplate) {
        this.template = dataAccessTemplate;
    }

    @Override
    public void save(Dates objectToBeSaved) {
        template.save(objectToBeSaved);
    }

    @Override
    public Dates fetch(Dates objectWhichShouldBeFilledWithMoreInformation) {
        return (Dates) template.getUniqueResult(FIND_DATES_BY_DATE, new String[]{"date"}, new Object[]{objectWhichShouldBeFilledWithMoreInformation.date()});
    }

    @Override
    public List<Dates> fetchAll() {
        return template.loadAll(Dates.class);
    }

    @Override
    public void flush() {
        template.flush();
    }
}
