package com.monthlychallenge.adapter.in.rest.mapper;

import com.monthlychallenge.adapter.in.rest.dto.request.CreateChallengeRequest;
import com.monthlychallenge.adapter.in.rest.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.application.dto.ChallengeResponse;
import com.monthlychallenge.application.dto.ChallengeTemplateResponse;
import com.monthlychallenge.application.ports.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.ports.in.command.UpdateChallengeCommand;
import com.monthlychallenge.domain.models.Challenge;
import com.monthlychallenge.domain.models.ChallengeTarget;
import com.monthlychallenge.domain.models.ChallengeTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChallengeWebMapper {

    public CreateChallengeCommand toCreateCommand(CreateChallengeRequest r) {
        ChallengeTarget target = r.getTargetValue() != null
                ? new ChallengeTarget(r.getTargetValue(), r.getTargetUnit()) : null;
        return new CreateChallengeCommand(r.getTitle(), r.getDescription(), r.getCategory(),
                r.getFrequency(), target, r.getMonth(), r.getVisibility(),
                r.getReminderHour(), r.getReminderMinute(), r.getWeeklyDueDays(), r.getMonthlyDueDay());
    }

    public UpdateChallengeCommand toUpdateCommand(UpdateChallengeRequest r) {
        ChallengeTarget target = r.getTargetValue() != null
                ? new ChallengeTarget(r.getTargetValue(), r.getTargetUnit()) : null;
        return new UpdateChallengeCommand(r.getTitle(), r.getDescription(), r.getCategory(),
                target, r.getVisibility(), r.getReminderHour(), r.getReminderMinute(),
                r.getWeeklyDueDays(), r.getMonthlyDueDay());
    }

    public ChallengeResponse toResponse(Challenge c) {
        return new ChallengeResponse(c.getId(), c.getTitle(), c.getDescription(),
                c.getCategory(), c.getFrequency(), c.getMonth(), c.getVisibility(),
                c.getTarget() != null ? c.getTarget().getValue() : null,
                c.getTarget() != null ? c.getTarget().getUnit()  : null,
                c.getReminderHour(), c.getReminderMinute(),
                c.getWeeklyDueDays(), c.getMonthlyDueDay(), c.isActive());
    }

    public ChallengeTemplateResponse toTemplateResponse(ChallengeTemplate t) {
        return new ChallengeTemplateResponse(t.getId(), t.getTitle(), t.getDescription(),
                t.getCategory(), t.getSuggestedFrequency(),
                t.getSuggestedTarget() != null ? t.getSuggestedTarget().getValue() : null,
                t.getSuggestedTarget() != null ? t.getSuggestedTarget().getUnit()  : null);
    }
}
