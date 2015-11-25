package org.opensrp.service.formSubmission.ziggy;

import static java.text.MessageFormat.format;
import static javax.script.ScriptContext.ENGINE_SCOPE;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.opensrp.service.formSubmission.handler.FormSubmissionRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZiggyService {
    public static final String JAVA_SCRIPT = "JavaScript";
    private static Logger logger = LoggerFactory.getLogger(ZiggyService.class.toString());
    private static final String SAVE_METHOD_NAME = "createOrUpdateEntity";
    private static final String JS_INIT_SCRIPT = "require([\"ziggy/FormDataController\"], function (FormDataController) {\n" +
            "    controller = FormDataController;\n" +
            "});";
    private static final String ZIGGY_FILE_LOADER = "ziggyFileLoader";
    private static final String REPOSITORY = "formDataRepositoryContext";
    private static final String FORM_SUBMISSION_ROUTER = "formSubmissionRouter";

    private ZiggyFileLoader ziggyFileLoader;
    private ZiggyDataHandler ziggyDataHandler;
    private FormSubmissionRouter formSubmissionRouter;
    private Object ziggyFormController;
    private Invocable invocable;

    @Autowired
    public ZiggyService(ZiggyFileLoader ziggyFileLoader,ZiggyDataHandler ziggyDataHandler,FormSubmissionRouter formSubmissionRouter) throws Exception {
        this.ziggyFileLoader = ziggyFileLoader;
        this.ziggyDataHandler = ziggyDataHandler;
        this.formSubmissionRouter = formSubmissionRouter;
        initRhino();
    }

    public boolean isZiggyCompliant(String entityType) {
		return ziggyDataHandler.isZiggyCompliant(entityType);
	}
    public void saveForm(String params, String formInstance) {
        try {
            invocable.invokeMethod(ziggyFormController, SAVE_METHOD_NAME, params, formInstance);
            logger.info(format("Saving form successful, with params: {0}.", params));
            logger.debug(format("Saving form successful, with params: {0}, with instance {1}.", params, formInstance));
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error(format("Form save failed, with params: {0}, with instance {1}. Exception: {2}", params, formInstance, e));
        }
    }

    private void initRhino() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(JAVA_SCRIPT);

        Bindings bindings = getBindings(engine);
        engine.setBindings(bindings, ENGINE_SCOPE);

        String jsFiles = ziggyFileLoader.getJSFiles();

        engine.eval(jsFiles);
        engine.eval(JS_INIT_SCRIPT);

        ziggyFormController = engine.get("controller");
        invocable = (Invocable) engine;
    }

    private Bindings getBindings(ScriptEngine engine) {
        Bindings bindings = engine.createBindings();
        bindings.put(ZIGGY_FILE_LOADER, ziggyFileLoader);
        bindings.put(REPOSITORY, ziggyDataHandler);
        bindings.put(FORM_SUBMISSION_ROUTER, formSubmissionRouter);
        return bindings;
    }
}
