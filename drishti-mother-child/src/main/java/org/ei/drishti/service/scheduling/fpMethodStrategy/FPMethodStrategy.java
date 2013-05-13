package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;

public interface FPMethodStrategy {
    void registerEC(FPProductInformation fpInfo);

    void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo);

    void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo);

    void renewFPProduct(FPProductInformation fpInfo);

    void fpFollowup(FPProductInformation fpInfo);
}
