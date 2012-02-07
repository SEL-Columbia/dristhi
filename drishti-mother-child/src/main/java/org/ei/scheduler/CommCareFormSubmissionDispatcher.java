package org.ei.scheduler;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.ei.commcare.listener.event.CommCareFormEvent;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_DATA_PARAMETER;
import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_NAME_PARAMETER;

@Component
public class CommCareFormSubmissionDispatcher {
    private Object routeEventsHere;

    public void registerForDispatch(Object dispatchToMethodsInThisObject) {
        this.routeEventsHere = dispatchToMethodsInThisObject;
    }

    @MotechListener(subjects = {CommCareFormEvent.EVENT_SUBJECT})
    public void handle(MotechEvent event) throws Exception {
        if (routeEventsHere == null) {
            return;
        }

        dispatch(event);
    }

    private void dispatch(MotechEvent event) throws IllegalAccessException, InvocationTargetException {
        Method method = findMethodWhichAcceptsOneParameter(event.getParameters().get(FORM_NAME_PARAMETER).toString());
        if (method == null) {
            return;
        }

        String jsonData = event.getParameters().get(FORM_DATA_PARAMETER).toString();
        Object parameter = getParameterFromData(method, jsonData);
        if (parameter == null) {
            return;
        }

        method.invoke(routeEventsHere, parameter);
    }

    private Method findMethodWhichAcceptsOneParameter(String methodName) {
        Method[] methods = routeEventsHere.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) {
                return method;
            }
        }

        return null;
    }

    private Object getParameterFromData(Method method, String jsonData) {
        try {
            return new Gson().fromJson(jsonData, method.getParameterTypes()[0]);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
