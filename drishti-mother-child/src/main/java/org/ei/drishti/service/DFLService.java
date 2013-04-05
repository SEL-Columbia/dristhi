package org.ei.drishti.service;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import static javax.script.ScriptContext.ENGINE_SCOPE;

public class DFLService {

    private static final String SAVE_METHOD_NAME = "save";
    private static final String JS_INIT_SCRIPT = "var formDataRepository = new enketo.FormDataRepository();\n" +
            "    var controller = new enketo.FormDataController(\n" +
            "        new enketo.EntityRelationshipLoader(),\n" +
            "        new enketo.FormDefinitionLoader(),\n" +
            "        new enketo.FormModelMapper(formDataRepository, new enketo.SQLQueryBuilder(formDataRepository), new enketo.IdFactory(new enketo.IdFactoryBridge())),\n" +
            "        formDataRepository);";
    private JSONFileLoader jsonFileLoader;

    public DFLService(JSONFileLoader jsonFileLoader) {
        this.jsonFileLoader = jsonFileLoader;
    }

    public void saveForm(String params, String formInstance) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        try {
            Bindings bindings = engine.createBindings();
            bindings.put("jsonFileLoader", jsonFileLoader);
            engine.setBindings(bindings, ENGINE_SCOPE);
            File jsFolder = new File("drishti-mother-child/js");
            System.out.println(jsFolder.getAbsoluteFile());
            File[] files = jsFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(".js");
                }
            });
            System.out.println(Arrays.toString(files));
            StringBuilder builder = new StringBuilder();
            for (File file : files) {
                builder.append(jsonFileLoader.load(file));
            }
            String jsFiles = builder.toString();

            engine.eval(jsFiles);
            engine.eval(JS_INIT_SCRIPT);

            Object formDataRepository = engine.get("controller");

            Invocable invocable = (Invocable) engine;

            invocable.invokeMethod(formDataRepository, SAVE_METHOD_NAME, params, formInstance);
            System.out.println(formDataRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
