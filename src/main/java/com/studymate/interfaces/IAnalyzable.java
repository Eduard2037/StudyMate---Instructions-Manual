package com.studymate.interfaces;

/**
 * Simple behaviour interface used for entities that can expose a numeric score
 * or KPI that can later be aggregated in analytics.
 */
public interface IAnalyzable {
    /**
     * @return a numeric score, percentage, or other metric representing this
     *         entity.
     */
    double computeScore();
}
