
package com.ancevt.d2d2world.script;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import com.ancevt.d2d2world.gameobject.IActioned;
import com.ancevt.d2d2world.gameobject.action.ActionProgram;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@Slf4j
public class JavaScriptEngine {

    public static final JavaScriptEngine INSTANCE = new JavaScriptEngine();

    private ScriptEngine scriptEngine;

    private JavaScriptEngine() {
        createEngineIfNotExists();
    }

    public @NotNull ActionProgram createActionProgram(@NotNull String javaScript) {
        createEngineIfNotExists();
        try {
            scriptEngine.eval(javaScript);
        } catch (ScriptException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private void createEngineIfNotExists() {
        if (scriptEngine == null) {
            scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
        }
    }

    public static void main(String[] args) {
        String javaScript = """
                                
                add
                                
                              
                """;
    }

    private static class JavaScriptInterface {

        private final IActioned gameObject;

        public JavaScriptInterface(IActioned gameObject) {
            this.gameObject = gameObject;
        }

        public void move() {

        }
    }

    public static float calculate(String source) {
        try {
            return Float.parseFloat(String.valueOf(INSTANCE.scriptEngine.eval(source)));
        } catch (ScriptException e) {
            throw new IllegalStateException(e);
        }
    }
}

/*
scriptEngine.eval("""

                var JavaClass = Java.type("com.ancevt.play.Play.JavaClass");

                var c = new JavaClass();

                c.hello = "A";
                c.hello += "b";

                print(c.hello);

                """);
 */






























