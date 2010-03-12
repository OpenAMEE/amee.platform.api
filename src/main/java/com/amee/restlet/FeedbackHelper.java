package com.amee.restlet;

import com.amee.base.cache.CacheService;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class FeedbackHelper {

    @Autowired
    private CacheService cacheService;

    private Feedback feedback = null;

    public Feedback getNewFeedback(Request request, Response response) {
        return getFeedback(request, response, true);
    }

    public Feedback getFeedbackAndClear(Request request, Response response) {
        Feedback feedback = getFeedback(request, response, false);
        if (feedback != null) {
            clearFeedback(request);
        }
        return feedback;
    }

    protected Feedback getFeedback(Request request, Response response, boolean create) {
        String feedbackUid;
        if (!create) {
            feedbackUid = getFeedbackUid(request);
            if (feedbackUid != null) {
                feedback = (Feedback) cacheService.get("Feedback", feedbackUid);
            }
        }
        if ((feedback == null) || create) {
            feedback = new Feedback();
            setFeedbackUid(response, feedback);
        }
        return feedback;
    }

    public void storeFeedback() {
        if (feedback != null) {
            cacheService.set("Feedback", feedback.getUid(), feedback);
        }
    }

    public void clearFeedback(Request request) {
        cacheService.delete("Feedback", getFeedbackUid(request));
        feedback = null;
    }

    protected String getFeedbackUid(Request request) {
        Cookie feedbackUidCookie = request.getCookies().getFirst("feedbackUid");
        if (feedbackUidCookie != null) {
            return feedbackUidCookie.getValue();
        } else {
            return null;
        }
    }

    protected void setFeedbackUid(Response response, Feedback feedback) {
        CookieSetting feedbackUidCookie = new CookieSetting(0, "feedbackUid", feedback.getUid(), "/", ".amee.com");
        CookieSetting oldFeedbackUidCookie = response.getCookieSettings().getFirst("feedbackUid");
        if (oldFeedbackUidCookie != null) {
            response.getCookieSettings().remove(oldFeedbackUidCookie);
        }
        response.getCookieSettings().add(feedbackUidCookie);
    }
}