package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.domain.FPProductInformation;
import org.springframework.stereotype.Component;

@Component
public class DefaultFPMethodStrategy implements FPMethodStrategy {
    @Override
    public void registerEC(FPProductInformation fpInfo) {
    }

    @Override
    public void unEnrollFromPreviousScheduleAsFPMethodChanged(FPProductInformation fpInfo) {
    }

    @Override
    public void enrollToNewScheduleForNewFPMethod(FPProductInformation fpInfo) {

    }

    @Override
    public void renewFPProduct(FPProductInformation fpInfo) {
    }

    @Override
    public void fpFollowup(FPProductInformation fpInfo) {
    }
}
