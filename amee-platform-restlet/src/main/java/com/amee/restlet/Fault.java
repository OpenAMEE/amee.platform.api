package com.amee.restlet;

import org.restlet.data.Status;

public interface Fault {

    String getMessage();

    Status getStatus();

    String getCode();
}
