package com.amee.restlet;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.*;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Representation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AMEEStatusServiceTest {

    private AMEEStatusService service;

    @Before
    public void setUp() {
        service = new AMEEStatusService();
    }

    @Test
    public void testGetTextRepresentation() {
        Status status = new Status(200);
        Request request = new Request();
        Response response = new Response(request);

        Representation result = service.getRepresentation(status, request, response);
        assertEquals(MediaType.TEXT_PLAIN, result.getMediaType());
    }

    @Test
    public void testGetJsonRepresentation() throws Exception {
        Status status = new Status(200);
        Request request = new Request();
        Response response = new Response(request);

        List<Preference<MediaType>> mediaTypes = new ArrayList<Preference<MediaType>>();
        Preference<MediaType> pref = new Preference<MediaType>(MediaType.APPLICATION_JSON);
        mediaTypes.add(pref);

        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setAcceptedMediaTypes(mediaTypes);
        request.setClientInfo(clientInfo);
        Representation result = service.getRepresentation(status, request, response);
        assertEquals(MediaType.APPLICATION_JSON, result.getMediaType());
        assertEquals(JsonRepresentation.class, result.getClass());
        JSONObject statusObject = (JSONObject) ((JsonRepresentation) result).toJsonObject().get("status");
        assertEquals(200, statusObject.get("code"));
    }
}
