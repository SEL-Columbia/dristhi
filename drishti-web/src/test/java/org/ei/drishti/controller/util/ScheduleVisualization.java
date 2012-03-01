package org.ei.drishti.controller.util;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.scheduletracking.api.domain.WindowName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.text.MessageFormat.format;

public class ScheduleVisualization {
    private final FakeSchedule schedule;
    private final String outputDir;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd, yyyy");
    private List<AlertInformation> alertInformation;

    public ScheduleVisualization(FakeSchedule schedule, String outputDir) {
        this.schedule = schedule;
        this.outputDir = outputDir;

        this.alertInformation = new ArrayList<AlertInformation>();
    }

    public void outputTo(String fileName, int numberOfTimelines) throws IOException {
        if (outputDir == null) {
            System.err.println("Not outputting schedule HTML as the path is invalid.");
            return;
        }
        StringBuilder outputHtml = new StringBuilder();

        outputHtml.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("  <head>\n")
                .append("    <script src=\"http://github.com/DmitryBaranovskiy/raphael/raw/master/raphael-min.js\"></script>\n")
                .append("    <script src=\"schedule.js\"></script>\n")
                .append("    <script type=\"text/javascript\">\n")
                .append("      window.onload = function () {\n")
                .append("        var schedule = new Schedule(" + numberOfTimelines + ").draw();\n");

        for (AlertInformation alert : alertInformation) {
            String methodName = "addAlerts";
            if (alert.hasPartialTimes()) {
                methodName = "addAlertsStartingWith";
            }

            String datesAsString = "";
            for (Date time : alert.times()) {
                datesAsString += format("new Date(\"{0}\"), ", formatter.print(new LocalDate(time)));
            }
            outputHtml.append(format("schedule.{0}(\"{1}\", \"{2}\", [{3}]);\n", methodName, alert.milestoneName(), alert.description(), datesAsString));
        }

        outputHtml.append("      };\n")
                .append("    </script>\n")
                .append("  </head>\n")
                .append("<body><center>" + schedule.name() + "</center></body>")
                .append("</html>");

        FileUtils.writeStringToFile(new File(outputDir, fileName), outputHtml.toString());
    }

    public void addAlerts(String milestoneName, WindowName window, Date[] dates) {
        alertInformation.add(new AlertInformation(milestoneName, window, Arrays.asList(dates), false));
    }

    public void addPartialAlerts(String milestoneName, WindowName window, Date[] dates) {
        alertInformation.add(new AlertInformation(milestoneName, window, Arrays.asList(dates), true));
    }
}
