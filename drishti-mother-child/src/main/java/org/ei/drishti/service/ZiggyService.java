package org.ei.drishti.service;

import org.ei.drishti.repository.FormDataRepository;
import org.ei.drishti.service.formSubmissionHandler.FormSubmissionRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static java.text.MessageFormat.format;
import static javax.script.ScriptContext.ENGINE_SCOPE;

@Service
public class ZiggyService {
    public static final String JAVA_SCRIPT = "JavaScript";
    private static Logger logger = LoggerFactory.getLogger(ZiggyService.class.toString());
    private static final String SAVE_METHOD_NAME = "save";
    private static final String JS_INIT_SCRIPT = "var formDataRepository = new enketo.FormDataRepository();\n" +
            "    var controller = new enketo.FormDataController(\n" +
            "        new enketo.EntityRelationshipLoader(),\n" +
            "        new enketo.FormDefinitionLoader(),\n" +
            "        new enketo.FormModelMapper(formDataRepository, new enketo.SQLQueryBuilder(formDataRepository), new enketo.IdFactory(new enketo.IdFactoryBridge())),\n" +
            "        formDataRepository, new enketo.FormSubmissionRouter());";
    private static final String ZIGGY_FILE_LOADER = "ziggyFileLoader";
    private static final String REPOSITORY = "formDataRepositoryContext";
    private static final String FORM_SUBMISSION_ROUTER = "formSubmissionRouter";

    private ZiggyFileLoader ziggyFileLoader;
    private FormDataRepository dataRepository;
    private FormSubmissionRouter formSubmissionRouter;
    private Object ziggyFormController;
    private Invocable invocable;

    @Autowired
    public ZiggyService(ZiggyFileLoader ziggyFileLoader, FormDataRepository dataRepository, FormSubmissionRouter formSubmissionRouter) throws Exception {
        this.ziggyFileLoader = ziggyFileLoader;
        this.dataRepository = dataRepository;
        this.formSubmissionRouter = formSubmissionRouter;
        initRhino();
    }

    public void saveForm(String params, String formInstance) {
        try {
            invocable.invokeMethod(ziggyFormController, SAVE_METHOD_NAME, params, formInstance);
            logger.info(format("Saving form successful, with params: {0}.", params));
            logger.debug(format("Saving form successful, with params: {0}, with instance {1}.", params, formInstance));
        } catch (Exception e) {
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
        bindings.put(REPOSITORY, dataRepository);
        bindings.put(FORM_SUBMISSION_ROUTER, formSubmissionRouter);
        return bindings;
    }
}
