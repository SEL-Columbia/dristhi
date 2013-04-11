package org.ei.drishti.service;

import org.ei.drishti.repository.FormDataRepository;
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
public class DFLService {
    private static Logger logger = LoggerFactory.getLogger(DFLService.class.toString());
    private static final String SAVE_METHOD_NAME = "save";
    private static final String JS_INIT_SCRIPT = "var formDataRepository = new enketo.FormDataRepository();\n" +
            "    var controller = new enketo.FormDataController(\n" +
            "        new enketo.EntityRelationshipLoader(),\n" +
            "        new enketo.FormDefinitionLoader(),\n" +
            "        new enketo.FormModelMapper(formDataRepository, new enketo.SQLQueryBuilder(formDataRepository), new enketo.IdFactory(new enketo.IdFactoryBridge())),\n" +
            "        formDataRepository);";
    private static final String DFL_FILE_LOADER = "dflFileLoader";
    private static final String REPOSITORY = "formDataRepositoryContext";

    private DFLFileLoader dflFileLoader;
    private FormDataRepository dataRepository;
    private Object dflFormController;
    private Invocable invocable;

    @Autowired
    public DFLService(DFLFileLoader dflFileLoader, FormDataRepository dataRepository) throws Exception {
        this.dflFileLoader = dflFileLoader;
        this.dataRepository = dataRepository;
        initRhino();
    }

    public void saveForm(String params, String formInstance) {
        try {
            invocable.invokeMethod(dflFormController, SAVE_METHOD_NAME, params, formInstance);
        } catch (Exception e) {
            logger.error(format("Form save failed, with params: {0}, with instance {1}. Exception: {2}", params, formInstance, e));
        }
    }

    private void initRhino() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        Bindings bindings = getBindings(engine);
        engine.setBindings(bindings, ENGINE_SCOPE);

        String jsFiles = dflFileLoader.getJSFiles();

        engine.eval(jsFiles);
        engine.eval(JS_INIT_SCRIPT);

        dflFormController = engine.get("controller");
        invocable = (Invocable) engine;
    }

    private Bindings getBindings(ScriptEngine engine) {
        Bindings bindings = engine.createBindings();
        bindings.put(DFL_FILE_LOADER, dflFileLoader);
        bindings.put(REPOSITORY, dataRepository);
        return bindings;
    }
}
