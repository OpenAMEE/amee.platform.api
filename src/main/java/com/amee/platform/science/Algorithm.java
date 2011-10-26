package com.amee.platform.science;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public interface Algorithm {

    String getLabel();

    String getContent();

    CompiledScript getCompiledScript(ScriptEngine engine) throws ScriptException;
}
