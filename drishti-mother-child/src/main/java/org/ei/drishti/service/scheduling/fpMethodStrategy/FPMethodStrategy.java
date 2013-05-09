package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;

public interface FPMethodStrategy {
    void registerEC(FPProductInformation fpInfo);

    void unEnrollFromRefillScheduleAsFPMethodChanged(FPProductInformation fpInfo);

    void enrollToRefillScheduleForNewFPMethod(FPProductInformation fpInfo);

    void renewFPProduct(FPProductInformation fpInfo);
}
