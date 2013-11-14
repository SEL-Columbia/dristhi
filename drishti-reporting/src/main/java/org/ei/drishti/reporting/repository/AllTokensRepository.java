package org.ei.drishti.reporting.repository;


import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.reporting.domain.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllTokensRepository {
    private static final String NAME_PARAMETER = "name";
    private static final String AGGREGATE_REPORTS_TOKEN_NAME = "aggregate-reports-token";
    private DataAccessTemplate dataAccessTemplate;

    @Autowired
    public AllTokensRepository(@Qualifier("serviceProvidedDataAccessTemplate")
                               DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public Integer getAggregateReportsToken() {
        Token token = (Token) dataAccessTemplate
                .getUniqueResult(Token.FIND_TOKEN_BY_NAME,
                        new String[]{NAME_PARAMETER},
                        new String[]{AGGREGATE_REPORTS_TOKEN_NAME});
        return token == null ? 0 : IntegerUtil.tryParse(token.value(), 0);
    }
}
