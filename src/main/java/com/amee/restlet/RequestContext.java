package com.amee.restlet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Request;
import org.restlet.data.Status;

import java.util.Iterator;
import java.util.Map;

/**
 * A simple bean for holding contextual information about the request.
 * <p/>
 * Its intended use is in debug statements etc.
 */
public class RequestContext {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Logger transactions = LoggerFactory.getLogger("transactions");

    private String requestPath = "";
    private String method = "";
    private String requestParameters = "";
    private String formParameters = "";
    private String error = "";
    private String more = "";
    private long start = 0L;
    private int status = 200;
    private String userUid = "";

    public RequestContext() {
        this.start = System.currentTimeMillis();
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
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

    private String getParameters(Form parameters) {
        Iterator<Parameter> i = parameters.iterator();
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

    /**
     * Echos incoming parameters map to a double underscore separated name/value pair and then comma separated String.
     * <p/>
     * For example ['aaa' -> 'ccc', 'eee' -> 'yyy] becomes 'aaa__ccc, eee_yyy'.
     *
     * @param parameters to convert to comma separated String
     * @return comma separated String
     */
    private String getParameters(Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry : parameters.entrySet()) {
            sb.append(entry.getKey());
            sb.append("__");
            if (!entry.getValue().equals("password")) {
                sb.append(entry.getValue());
            } else {
                sb.append("XXXXXX");
            }
            sb.append(", ");
        }
        // Remove the final trailing separator and space
        return StringUtils.chomp(sb.toString(), ", ");
    }

    public void setError(String error) {
        if (StringUtils.isNotBlank(error)) {
            this.error = error;
        }
    }

    /**
     * Store a string representation of the formParameters parameters.
     *
     * @param formParameters The Form keys and values.
     */
    public void setFormParameters(Form formParameters) {
        this.formParameters = getParameters(formParameters);
    }

    /**
     * Store a string representation of the formParameters parameters.
     *
     * @param params A Map of formParameters parameters keys and values.
     */
    public void setForm(Map<String, String> params) {
        this.formParameters = getParameters(params);
    }

    public void setStatus(Status status) {
        this.status = status.getCode();
    }

    public void error() {
        log.error(toString());
    }

    public void record() {
        transactions.info(toString());
    }

    /**
     * Returns a description of this RequestContext suitable for logging.
     * The exact details are subject to change but the following may be considered typical:
     * <p/>
     * "USER_UID|REQUEST_PATH|REQUEST_PARAMS|FORM_PARAMS|ERROR_MSG|METHOD|ADDITIONAL_INFO|STATUS|DURATION"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userUid).append("|");
        sb.append(requestPath).append("|");
        sb.append(requestParameters).append("|");
        sb.append(formParameters).append("|");
        sb.append(error).append("|");
        sb.append(method).append("|");
        sb.append(more).append("|");
        sb.append(status).append("|");
        sb.append(System.currentTimeMillis() - start);
        return sb.toString();
    }
}
