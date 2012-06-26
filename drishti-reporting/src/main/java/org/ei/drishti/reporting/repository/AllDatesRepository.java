package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.Dates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.ei.drishti.reporting.domain.Dates.FIND_DATES_BY_DATE;

@Repository
@Transactional
public class AllDatesRepository {
    private DataAccessTemplate template;

    public AllDatesRepository() {
    }

    @Autowired
    public AllDatesRepository(DataAccessTemplate template) {
        this.template = template;
    }

    public void save(Date date) {
        template.save(new Dates(date));
    }

    public Dates fetch(Date date) {
        return (Dates) template.getUniqueResult(FIND_DATES_BY_DATE, new String[] {"date"}, new Object[] {date});
    }
}
