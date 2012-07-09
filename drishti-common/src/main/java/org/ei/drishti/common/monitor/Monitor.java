package org.ei.drishti.common.monitor;

import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Monitor {
    private static Logger logger = LoggerFactory.getLogger("DRISHTI_MONITOR");

    public Probe start(Metric metric) {
        return new Probe(metric);
    }

    public void end(Probe probe) {
        addObservationFor(probe.metric(), probe.value());
    }

    public void addObservationFor(Metric metric, long value) {
        logger.info(metric.name() + " " + value + " " + DateUtil.now().getMillis());
    }
}
