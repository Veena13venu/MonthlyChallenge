package com.monthlychallenge.application.usecase;

import com.monthlychallenge.application.ports.in.CheckInUseCase;
import com.monthlychallenge.application.ports.in.command.RecordCheckInCommand;
import com.monthlychallenge.application.ports.out.CheckInRepository;
import com.monthlychallenge.application.ports.out.UserRepository;
import com.monthlychallenge.domain.exceptions.ResourceNotFoundException;
import com.monthlychallenge.domain.models.CheckIn;
import com.monthlychallenge.domain.models.DaySummary;
import com.monthlychallenge.domain.models.User;
import com.monthlychallenge.domain.service.DaySummaryDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CheckInService implements CheckInUseCase {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final ChallengeService challengeService;
    private final DaySummaryDomainService daySummaryDomainService;

    public CheckInService(CheckInRepository checkInRepository,
                          UserRepository userRepository,
                          ChallengeService challengeService,
                          DaySummaryDomainService daySummaryDomainService) {
        this.checkInRepository = checkInRepository;
        this.userRepository = userRepository;
        this.challengeService = challengeService;
        this.daySummaryDomainService = daySummaryDomainService;
    }

    @Override
    public CheckIn recordCheckIn(UUID userId, RecordCheckInCommand cmd) {
        LocalDate today = LocalDate.now();
        CheckIn checkIn = checkInRepository
                .findByUserIdAndChallengeIdAndDate(userId, cmd.challengeId(), today)
                .orElseGet(() -> CheckIn.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .challengeId(cmd.challengeId())
                        .date(today)
                        .createdAt(Instant.now())
                        .build());

        checkIn.setStatus(cmd.status());
        checkIn.setActualValue(cmd.actualValue());
        checkIn.setUpdatedAt(Instant.now());
        return checkInRepository.save(checkIn);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckIn> getCheckInsForDate(UUID userId, LocalDate date) {
        return checkInRepository.findByUserIdAndDate(userId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public DaySummary getLiveDaySummary(UUID userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CheckIn> checkIns = checkInRepository.findByUserIdAndDate(userId, date);
        int totalDue = challengeService.getTodaysChallenges(userId).size();
        return daySummaryDomainService.computeDaySummary(userId, date, checkIns, totalDue, user.getMinimumDailyTarget());
    }
}
