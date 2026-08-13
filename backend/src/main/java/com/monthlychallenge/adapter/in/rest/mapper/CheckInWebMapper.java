package com.monthlychallenge.adapter.in.rest.mapper;

import com.monthlychallenge.application.dto.CheckInResponse;
import com.monthlychallenge.application.dto.DaySummaryResponse;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.DaySummary;
import org.springframework.stereotype.Component;

@Component
public class CheckInWebMapper {
    public CheckInResponse toResponse(CheckIn ci) {
        return new CheckInResponse(ci.getId(), ci.getChallengeId(), ci.getDate(),
                ci.getStatus(), ci.getActualValue(), ci.pointValue());
    }
    public DaySummaryResponse toDaySummaryResponse(DaySummary ds) {
        return new DaySummaryResponse(ds.getDate(), ds.getTotalPoints(),
                ds.getMinimumThreshold(), ds.getResult());
    }
}
