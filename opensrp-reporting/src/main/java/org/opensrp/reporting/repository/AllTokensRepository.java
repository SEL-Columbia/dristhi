package org.opensrp.reporting.repository;


import org.opensrp.common.util.IntegerUtil;
import org.opensrp.reporting.domain.Token;
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

    public void saveAggregateReportsToken(Integer newToken) {
        Token token = (Token) dataAccessTemplate
                .getUniqueResult(Token.FIND_TOKEN_BY_NAME,
                        new String[]{NAME_PARAMETER},
                        new String[]{AGGREGATE_REPORTS_TOKEN_NAME});
        if (token == null) {
            dataAccessTemplate.save(new Token(AGGREGATE_REPORTS_TOKEN_NAME, newToken.toString()));
        } else {
            dataAccessTemplate.save(token.withValue(newToken.toString()));
        }
    }
}
