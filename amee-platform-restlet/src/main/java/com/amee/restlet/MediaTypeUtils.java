package com.amee.restlet;

import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.data.Request;

import java.util.List;

public class MediaTypeUtils {

    public final static MediaType APPLICATION_ECOSPOLD_XML =
            MediaType.register("application/x.ecospold+xml", "EcoSpold document");

    public static boolean isStandardWebBrowser(Request request) {
        return MediaTypeUtils.doesClientAccept(MediaType.TEXT_HTML, request) ||
                MediaTypeUtils.doesClientAccept(MediaType.APPLICATION_XHTML_XML, request) ||
                MediaTypeUtils.doesClientAccept(MediaType.ALL, request);
    }

    /**
     * Returns true if the supplied MediaType is for XML.
     *
     * @param mediaType to check
     * @return true if mediaType is XML
     */
    public static boolean isXML(MediaType mediaType) {
        return mediaType.equals(MediaType.APPLICATION_XML) || mediaType.equals(APPLICATION_ECOSPOLD_XML);
    }

    /**
     * Returns true if the supplied MediaType is for JSON.
     *
     * @param mediaType to check
     * @return true if mediaType is JSON
     */
    public static boolean isJSON(MediaType mediaType) {
        return mediaType.equals(MediaType.APPLICATION_JSON);
    }

    /**
     * Returns true if the MediaType for the request supports JSON.
     *
     * @param request to check MediaType for.
     * @return true if the request is for JSON.
     */
    public static boolean doesClientAcceptJSON(Request request) {
        return MediaTypeUtils.doesClientAccept(MediaType.APPLICATION_JSON, request);
    }

    /**
     * Returns true if the MediaType for the request supports XML.
     *
     * @param request to check MediaType for.
     * @return true if the request is for XML.
     */
    public static boolean doesClientAcceptXML(Request request) {
        return MediaTypeUtils.doesClientAccept(MediaType.APPLICATION_XML, request) ||
                MediaTypeUtils.doesClientAccept(APPLICATION_ECOSPOLD_XML, request);
    }

    // this ignores Preference 'quality'

    public static boolean doesClientAccept(MediaType mediaType, Request request) {
        List<Preference<MediaType>> mediaTypePrefs =
                request.getClientInfo().getAcceptedMediaTypes();
        for (Preference<MediaType> mediaTypePref : mediaTypePrefs) {
            if (mediaTypePref.getMetadata().equals(mediaType)) {
                return true;
            }
        }
        return false;
    }

    public static void forceMediaType(MediaType mediaType, Request request) {
        request.getClientInfo().getAcceptedMediaTypes().clear();
        request.getClientInfo().getAcceptedMediaTypes().add(
                new Preference<MediaType>(mediaType));
    }
}
