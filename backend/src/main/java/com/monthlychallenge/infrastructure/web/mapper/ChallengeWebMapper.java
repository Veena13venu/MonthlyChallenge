package com.monthlychallenge.infrastructure.web.mapper;

import com.monthlychallenge.application.port.in.command.CreateChallengeCommand;
import com.monthlychallenge.application.port.in.command.UpdateChallengeCommand;
import com.monthlychallenge.domain.model.Challenge;
import com.monthlychallenge.domain.model.ChallengeTarget;
import com.monthlychallenge.domain.model.ChallengeTemplate;
import com.monthlychallenge.infrastructure.web.dto.request.CreateChallengeRequest;
import com.monthlychallenge.infrastructure.web.dto.request.UpdateChallengeRequest;
import com.monthlychallenge.infrastructure.web.dto.response.ChallengeResponse;
import com.monthlychallenge.infrastructure.web.dto.response.ChallengeTemplateResponse;
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
