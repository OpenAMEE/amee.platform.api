package com.amee.base.domain;

import java.util.List;

/**
 * Wraps a {@link List} of {@link E} instances resulting from a database or other query. The purpose is to provide
 * context around the limited sub-set of the entire result set that may have been returned. The truncated flag
 * indicates if only part of the entire possible result set was returned, whereas the hits property indicates the
 * potential maximum number of matching results.
 * <p/>
 * This class does not guarantee that any of the properties will definitely be set. All or any of the properties may
 * be null. Clients are expected to have some understanding of the context in which instances have been created.
 * <p/>
 * The resultStart and resultLimit properties provide context of similar parameters that may have been used
 * in a query. They indicate the starting index and result limit, respectively, of the result sub-set from the overall
 * list of potential results for the query.
 *
 * @param <E> the type of the expected results
 */
public class ResultsWrapper<E> {

    /**
     * A {@link List} of {@link E} instances.
     */
    private List<E> results;

    /**
     * A flag indicating if the result {@link List} was truncated, such that all the potential result instances
     * where not actually returned.
     */
    private boolean truncated = false;

    /**
     * The starting index of the result list within the overall result set. Expected to be zero-based.
     */
    private int resultStart;

    /**
     * The maximum number of results to return.
     */
    private int resultLimit;

    /**
     * The number of 'hits' in the overall potential result set. This may be a vague or exact number.
     */
    private int hits;

    /**
     * Constructs a ResultsWrapper with all properties set to null.
     */
    public ResultsWrapper() {
        super();
    }

    /**
     * Constructs a ResultsWrapper with the supplied results {@link ResultsWrapper} and truncated state.
     *
     * @param results   {@link List} or results
     * @param truncated the truncated state
     */
    public ResultsWrapper(List<E> results, boolean truncated) {
        this();
        setResults(results);
        setTruncated(truncated);
        setHits(results.size());
    }

    /**
     * Constructs a ResultsWrapper with the supplied results {@link ResultsWrapper} and truncated state.
     *
     * @param results     {@link List} or results
     * @param truncated   the truncated state
     * @param resultStart tje starting index of the result list within the overall result set
     * @param resultLimit the maximum number of results to return
     * @param hits        the number of hits
     */
    public ResultsWrapper(List<E> results, boolean truncated, int resultStart, int resultLimit, int hits) {
        this(results, truncated);
        setResultStart(resultStart);
        setResultLimit(resultLimit);
        setHits(hits);
    }

    /**
     * Get the result {@link List} of {@link E} instances.
     *
     * @return result {@link List} of {@link E} instances
     */
    public List<E> getResults() {
        return results;
    }

    /**
     * Set the result {@link List} of {@link E} instances.
     *
     * @param results {@link List} of {@link E} instances
     */
    public void setResults(List<E> results) {
        this.results = results;
    }

    /**
     * Get the truncated flag.
     *
     * @return truncated flag
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * Set the truncated flag.
     *
     * @param truncated flag
     */
    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    /**
     * Return the resultLimit, which is the maximum number of results to return.
     *
     * @return the resultLimit
     */
    public int getResultStart() {
        return resultStart;
    }

    /**
     * Sets the resultStart property.
     *
     * @param resultStart the resultStart property
     */
    public void setResultStart(int resultStart) {
        this.resultStart = resultStart;
    }

    /**
     * Gets the resultLimit property, which is the maximum number of results to return.
     *
     * @return the resultLimit property
     */
    public int getResultLimit() {
        return resultLimit;
    }

    /**
     * Sets the resultLimit property.
     *
     * @param resultLimit the resultLimit property
     */
    public void setResultLimit(int resultLimit) {
        this.resultLimit = resultLimit;
    }

    /**
     * Get the hits property.
     *
     * @return hits property
     */
    public int getHits() {
        return hits;
    }

    /**
     * Set the hits property.
     *
     * @param hits the hits property
     */
    public void setHits(int hits) {
        this.hits = hits;
    }
}
