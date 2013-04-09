package org.ei.drishti.service;

import org.ei.drishti.repository.FormDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String SAVE_METHOD_NAME = "save";
    private static final String JS_INIT_SCRIPT = "var formDataRepository = new enketo.FormDataRepository();\n" +
            "    var controller = new enketo.FormDataController(\n" +
            "        new enketo.EntityRelationshipLoader(),\n" +
            "        new enketo.FormDefinitionLoader(),\n" +
            "        new enketo.FormModelMapper(formDataRepository, new enketo.SQLQueryBuilder(formDataRepository), new enketo.IdFactory(new enketo.IdFactoryBridge())),\n" +
            "        formDataRepository);";
    private JSONFileLoader jsonFileLoader;
    private FormDataRepository dataRepository;

    @Autowired
    public DFLService(JSONFileLoader jsonFileLoader, FormDataRepository dataRepository) {
        this.jsonFileLoader = jsonFileLoader;
        this.dataRepository = dataRepository;
    }

    public void saveForm(String params, String formInstance) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            Bindings bindings = engine.createBindings();
            bindings.put("jsonFileLoader", jsonFileLoader);
            bindings.put("repository", dataRepository);
            engine.setBindings(bindings, ENGINE_SCOPE);

            String jsFiles = getJSFiles();

            engine.eval(jsFiles);
            engine.eval(JS_INIT_SCRIPT);

            Object formDataRepository = engine.get("controller");
            Invocable invocable = (Invocable) engine;
            invocable.invokeMethod(formDataRepository, SAVE_METHOD_NAME, params, formInstance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getJSFiles() throws IOException {
        File jsFolder = new File("drishti-mother-child/js");
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
