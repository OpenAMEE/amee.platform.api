package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class MockAlgorithm implements Algorithm {

    private String content = "";

    public String getLabel() {
        return "A mock Algorithm.";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CompiledScript getCompiledScript(ScriptEngine engine) throws ScriptException {
        if (StringUtils.isBlank(getContent())) {
            throw new AlgorithmException(
                    "Algorithm content is null (" + getLabel() + ").");
        }
        CompiledScript compiledScript = ((Compilable) engine).compile(getContent());
        if (compiledScript == null) {
            throw new AlgorithmException(
                    "CompiledScript is null (" + getLabel() + ").");
        }
        return compiledScript;
    }
}
