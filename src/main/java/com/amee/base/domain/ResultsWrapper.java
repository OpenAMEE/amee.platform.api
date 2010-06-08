package com.amee.base.domain;

import java.util.List;

public class ResultsWrapper<E> {

    private List<E> results;
    private boolean truncated = false;

    public ResultsWrapper() {
        super();
    }

    public ResultsWrapper(List<E> results, boolean truncated) {
        this();
        setResults(results);
        setTruncated(truncated);
    }

    public List<E> getResults() {
        return results;
    }

    public void setResults(List<E> results) {
        this.results = results;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }
}
