package com.amee.restlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Request;

import java.util.Iterator;

/**
 * A simple bean for holding contextual information about the request.
 * <p/>
 * Its intended use is in debug statements etc.
 */
public class RequestContext {

    private final Log log = LogFactory.getLog(getClass());
    private final Log transactions = LogFactory.getLog("transactions");

    private String requestPath = "";
    private String method = "";
    private String requestParameters = "";
    private String error = "";
    private String form = "";
    private String label = "";
    private String type = "";
    private String more = "";
    private long start = 0L;

    public RequestContext() {
        this.start = System.currentTimeMillis();
    }

    public void setMore(String more) {
        if (StringUtils.isNotBlank(more)) {
            this.more = more;
        }
    }

    public void addMore(String more) {
        if (StringUtils.isNotBlank(more)) {
            if (StringUtils.isBlank(this.more)) {
                this.more = more;
            } else {
                this.more = this.more + "|" + more;
            }
        }
    }

    public void setRequest(Request request) {
        this.requestPath = request.getResourceRef().getPath();
        this.method = request.getMethod().toString();
        this.requestParameters = getParameters(request.getResourceRef().getQueryAsForm());
    }

    private String getParameters(Form parmameters) {
        Iterator<Parameter> i = parmameters.iterator();
        if (!i.hasNext())
            return "";

        StringBuilder sb = new StringBuilder();
        for (; ;) {
            Parameter p = i.next();
            sb.append(p.getName());
            sb.append("__");
            if (!p.getName().equals("password")) {
                sb.append(p.getValue());
            } else {
                sb.append("XXXXXX");
            }
            if (i.hasNext()) {
                sb.append(", ");
            } else {
                return sb.toString();
            }
        }
    }

    public void setError(String error) {
        if (StringUtils.isNotBlank(error)) {
            this.error = error;
        }
    }

    public void setForm(Form form) {
        this.form = getParameters(form);
    }

    public void error() {
        log.error(toString());
    }

    public void record() {
        transactions.info(toString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(requestPath).append("|");
        sb.append(type).append("|");
        sb.append(label).append("|");
        sb.append(requestParameters.replace("=", "__")).append("|");
        sb.append(form).append("|");
        sb.append(error).append("|");
        sb.append(method).append("|");
        sb.append(more).append("|");
        sb.append(System.currentTimeMillis() - start);
        return sb.toString();
    }
}
