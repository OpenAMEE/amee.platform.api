package com.amee.restlet;

import org.restlet.data.Status;

public interface Fault {

    public String getMessage();

    public Status getStatus();

    public String getCode();
}
