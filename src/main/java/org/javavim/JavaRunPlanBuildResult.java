package org.javavim;

/**
 * Result object for Java run plan creation.
 */
public record JavaRunPlanBuildResult(JavaRunPlan plan, String errorMessage) {

    /**
     * Returns true when plan creation succeeded.
     */
    public boolean isSuccess() {
        return plan != null;
    }

    public static JavaRunPlanBuildResult success(JavaRunPlan plan) {
        return new JavaRunPlanBuildResult(plan, null);
    }

    public static JavaRunPlanBuildResult error(String message) {
        return new JavaRunPlanBuildResult(null, message);
    }
}
