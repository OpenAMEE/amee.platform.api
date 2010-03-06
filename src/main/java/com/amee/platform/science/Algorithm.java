package com.amee.platform.science;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public interface Algorithm {

    public String getLabel();

    public String getContent();

    public CompiledScript getCompiledScript(ScriptEngine engine) throws ScriptException;
}
