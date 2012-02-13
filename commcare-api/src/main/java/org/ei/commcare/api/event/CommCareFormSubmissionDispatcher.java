package org.ei.commcare.api.event;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.ei.commcare.api.event.CommCareFormEvent.FORM_DATA_PARAMETER;
import static org.ei.commcare.api.event.CommCareFormEvent.FORM_NAME_PARAMETER;

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

        String methodName = event.getParameters().get(FORM_NAME_PARAMETER).toString();
        String parameterJson = event.getParameters().get(FORM_DATA_PARAMETER).toString();
        dispatch(methodName, parameterJson);
    }

    private void dispatch(String methodName, String parameterJson) throws Exception {
        Method method = findMethodWhichAcceptsOneParameter(methodName);
        if (method == null) {
            return;
        }

        Object parameter = getParameterFromData(method, parameterJson);
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
