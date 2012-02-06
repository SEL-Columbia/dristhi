package org.ei.scheduler;

import org.ei.commcare.listener.event.CommCareListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommcareScheduler {
    public static void main(String[] args) throws Throwable {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
                new String[] {"applicationContext-drishti.xml", "integrationCommon.xml"});
        CommCareListener careListener = appContext.getBean(CommCareListener.class);
        while (true) {
            Thread.sleep(3000);
            System.out.println("Hello! Going for it!");
            careListener.fetchFromServer();
        }
    }
}
