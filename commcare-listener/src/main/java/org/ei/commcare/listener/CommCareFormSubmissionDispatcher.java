package org.ei.commcare.listener;

import com.google.gson.JsonParseException;
import org.ei.commcare.listener.event.CommCareFormEvent;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_DATA_PARAMETER;
import static org.ei.commcare.listener.event.CommCareFormEvent.FORM_NAME_PARAMETER;

@Component
public class CommCareFormSubmissionDispatcher {
    private Object routeEventsHere;
    private static Logger logger = Logger.getLogger(CommCareFormSubmissionDispatcher.class.toString());

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
            logger.warning("Cannot dispatch: Unable to find method: " + methodName + " in " + routeEventsHere.getClass());
            return;
        }

        Object parameter = getParameterFromData(method, parameterJson);
        if (parameter == null) {
            logger.warning("Cannot dispatch: Unable to convert JSON: " + parameterJson + " to object, to call method " + method);
            return;
        }

        logger.fine("Dispatching " + parameter + " to method: " + method + " in object: " + routeEventsHere);
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
            return new MotechJsonReader().readFromString(jsonData, method.getParameterTypes()[0]);
        } catch (JsonParseException e) {
            return null;
        }
    }
}
