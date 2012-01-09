package com.amee.base.resource;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * An interface defining a basic structure that all 'renderers' can conform to. A renderer is intended to transform
 * domain model entities and other required objects into an output representation of a specific media-type. For
 * example, a result list of objects from a database could be represented as a JSON output document.
 */
public interface ResourceRenderer {

    DateTimeFormatter DATE_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

    /**
     * Callback to indicate that the renderer implementation can start work.
     */
    void start();

    /**
     * Callback to indicate that the request succeeded.
     */
    void ok();

    /**
     * Return the media-type for the output representation from the renderer.
     *
     * @return the media-type as a string
     */
    String getMediaType();

    /**
     * Return the object of the output representation. Typically this will be a {@link org.json.JSONObject} or a
     * {@link org.jdom.Document}.
     *
     * @return the output representation object
     */
    Object getObject();
}
