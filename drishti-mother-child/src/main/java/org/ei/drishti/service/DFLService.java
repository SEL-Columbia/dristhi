package org.ei.drishti.service;

import org.ei.drishti.repository.FormDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

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
    private static final String JSON_FILE_LOADER = "jsonFileLoader";
    private static final String REPOSITORY = "repository";

    private JSONFileLoader jsonFileLoader;
    private FormDataRepository dataRepository;
    private Object dflFormController;
    private Invocable invocable;

    @Autowired
    public DFLService(JSONFileLoader jsonFileLoader, FormDataRepository dataRepository, @Value("#{drishti['js.files.path']}") String jsFilesPath) {
        this.jsonFileLoader = jsonFileLoader;
        this.dataRepository = dataRepository;
        initRhino(jsFilesPath);
    }

    public void saveForm(String params, String formInstance) {
        try {
            invocable.invokeMethod(dflFormController, SAVE_METHOD_NAME, params, formInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRhino(String jsFilesPath) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            Bindings bindings = getBindings(engine);
            engine.setBindings(bindings, ENGINE_SCOPE);

            String jsFiles = getJSFiles(jsFilesPath);

            engine.eval(jsFiles);
            engine.eval(JS_INIT_SCRIPT);

            dflFormController = engine.get("controller");
            invocable = (Invocable) engine;
        } catch (Exception e) {
            logger.error("Rhino initialization failed: " + e);
        }
    }

    private Bindings getBindings(ScriptEngine engine) {
        Bindings bindings = engine.createBindings();
        bindings.put(JSON_FILE_LOADER, jsonFileLoader);
        bindings.put(REPOSITORY, dataRepository);
        return bindings;
    }

    private String getJSFiles(String jsFilesPath) throws IOException {
        File jsFolder = new File(jsFilesPath);
        File[] files = jsFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".js");
            }
        });
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            builder.append(jsonFileLoader.load(file));
        }
        return builder.toString();
    }
}
