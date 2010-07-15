package com.amee.base.domain;

import java.util.List;

public class ResultsWrapper<E> {

    private List<E> results;
    private boolean truncated = false;
    private int resultStart;
    private int resultLimit;
    private int hits;

    public ResultsWrapper() {
        super();
    }

    public ResultsWrapper(List<E> results, boolean truncated) {
        this();
        setResults(results);
        setTruncated(truncated);
        setHits(results.size());
    }

    public ResultsWrapper(List<E> results, boolean truncated, int resultStart, int resultLimit, int hits) {
        this(results, truncated);
        setResultStart(resultStart);
        setResultLimit(resultLimit);
        setHits(hits);
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

    public int getResultStart() {
        return resultStart;
    }

    public void setResultStart(int resultStart) {
        this.resultStart = resultStart;
    }

    public int getResultLimit() {
        return resultLimit;
    }

    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }
}
