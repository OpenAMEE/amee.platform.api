package com.amee.restlet;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.dom.DocumentImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.Application;
import org.restlet.data.*;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.DomRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.service.StatusService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.logging.Level;

@Service("ameeStatusService")
public class AMEEStatusService extends StatusService {

    public AMEEStatusService() {
        super();
    }

    @Override
    public Status getStatus(Throwable throwable, Request request, Response response) {
        Application.getCurrent().getLogger()
                .log(Level.SEVERE, "Unhandled exception or error intercepted: " + throwable, throwable);
        return new Status(Status.SERVER_ERROR_INTERNAL.getCode(), throwable);
    }

    @Override
    public Representation getRepresentation(Status status, Request request, Response response) {
        Representation representation;
        if (MediaTypeUtils.isStandardWebBrowser(request)) {
            representation = getWebBrowserRepresentation(status, request);
        } else {
            representation = getApiRepresentation(status, request);
        }
        if (representation == null) {
            return super.getRepresentation(status, request, response);
        }
        return representation;
    }

    private Representation getWebBrowserRepresentation(Status status, Request request) {
        return null;
    }

    private Representation getApiRepresentation(Status status, Request request) {
        Representation representation = null;
        if (MediaTypeUtils.doesClientAcceptJSON(request)) {
            representation = getJsonRepresentation(status);
        } else if (MediaTypeUtils.doesClientAcceptXML(request)) {
            representation = getDomRepresentation(status);
        }
        if (representation == null) {
            representation = getStringRepresentation(status);
        }
        return representation;
    }

    private Representation getJsonRepresentation(Status status) {
        Representation representation = null;
        try {
            JSONObject obj = new JSONObject();
            JSONObject statusObj = new JSONObject();
            statusObj.put("code", status.getCode());
            statusObj.put("name", status.getName());
            statusObj.put("description", status.getDescription());
            statusObj.put("uri", status.getUri());
            obj.put("status", statusObj);
            representation = new JsonRepresentation(obj);
        } catch (JSONException e) {
            // swallow
        }
        return representation;
    }

    private Representation getDomRepresentation(Status status) {
        Representation representation;
        Document document = new DocumentImpl();
        Element elem = document.createElement("Resources");
        Element statusElem = document.createElement("Status");
        statusElem.appendChild(getElement(document, "Code", "" + status.getCode()));
        statusElem.appendChild(getElement(document, "Name", status.getName()));
        statusElem.appendChild(getElement(document, "Description", status.getDescription()));
        statusElem.appendChild(getElement(document, "URI", status.getUri()));
        elem.appendChild(statusElem);
        document.appendChild(elem);
        representation = new DomRepresentation(MediaType.APPLICATION_XML, document);
        return representation;
    }

    private static Element getElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        return element;
    }

    private Representation getStringRepresentation(Status status) {
        return new StringRepresentation(
                "Code: " + status.getCode() + "\n" +
                        "Name: " + status.getName() + "\n" +
                        "Description: " + status.getDescription() + "\n");
    }

    private String getNextUrl(Request request) {

        // first, look for 'next' in parameters
        Form parameters = request.getResourceRef().getQueryAsForm();
        String next = parameters.getFirstValue("next");

        if (StringUtils.isEmpty(next)) {
            // second, determine 'next' from the previousResourceRef, if set (by DataFilter and ProfileFilter perhaps)
            if (request.getAttributes().get("previousResourceRef") != null) {
                next = request.getAttributes().get("previousResourceRef").toString();
            }
        }

        if (StringUtils.isEmpty(next)) {
            // third, determine 'next' from current URL
            next = request.getResourceRef().toString();
            if ((next != null) && ((next.endsWith("/signIn") || next.endsWith("/signOut") || next.endsWith("/protected")))) {
                next = null;
            }
        }

        if (StringUtils.isEmpty(next)) {
            // forth, use a default
            next = "/auth";
        }
        return next;
    }
}

