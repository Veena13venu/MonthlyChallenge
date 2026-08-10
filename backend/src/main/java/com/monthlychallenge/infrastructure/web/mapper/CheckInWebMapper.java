package com.monthlychallenge.infrastructure.web.mapper;

import com.monthlychallenge.domain.model.CheckIn;
import com.monthlychallenge.domain.model.DaySummary;
import com.monthlychallenge.infrastructure.web.dto.response.CheckInResponse;
import com.monthlychallenge.infrastructure.web.dto.response.DaySummaryResponse;
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
