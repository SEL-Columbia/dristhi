package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class RulesFactory implements IRulesFactory, ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(RulesFactory.class.toString());

    private static final String RULES_PACKAGE_NAME = "org.ei.drishti.service.reporting.rules";
    private ApplicationContext applicationContext;

    public IRule ruleByName(String ruleName) throws RuleNotFoundException {
        try {
            return (IRule) applicationContext.getBean(Class.forName(RULES_PACKAGE_NAME + "." + ruleName));
        } catch (Exception e) {
            logger.error(e.getMessage() + getFullStackTrace(e));
            throw new RuleNotFoundException(ruleName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
