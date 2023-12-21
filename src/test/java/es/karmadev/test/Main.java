package es.karmadev.test;

import es.karmadev.api.script.ScriptParser;
import es.karmadev.api.script.body.ScriptBody;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Throwable {
        Path file = Paths.get("E:\\Projects\\java\\KScript\\src\\test\\resources\\test_script.kls");
        ScriptParser parser = new ScriptParser(file);
        ScriptBody body = parser.getBody();

        /*System sys = System.getImport();
        Function function = sys.getFunction("println", 1);
        if (function == null) return;

        function.execute(body);*/

        body.init();
    }
}
