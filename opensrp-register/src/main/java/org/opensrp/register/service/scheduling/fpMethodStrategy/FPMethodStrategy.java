package org.opensrp.register.service.scheduling.fpMethodStrategy;

import org.opensrp.domain.FPProductInformation;

public interface FPMethodStrategy {
    void registerEC(FPProductInformation fpInfo);

    void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo);

    void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo);

    void renewFPProduct(FPProductInformation fpInfo);

    void fpFollowup(FPProductInformation fpInfo);
}
