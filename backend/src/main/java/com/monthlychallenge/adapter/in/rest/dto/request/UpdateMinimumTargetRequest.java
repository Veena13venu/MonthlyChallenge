package com.monthlychallenge.adapter.in.rest.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdateMinimumTargetRequest {
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private double value;
    private boolean isPercentage;

    public double getValue()             { return value; }
    public void setValue(double v)       { this.value = v; }
    public boolean isPercentage()        { return isPercentage; }
    public void setPercentage(boolean v) { this.isPercentage = v; }
}
