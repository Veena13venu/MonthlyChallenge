package com.monthlychallenge.application.service;

import com.monthlychallenge.application.port.in.CheckInUseCase;
import com.monthlychallenge.application.port.in.command.RecordCheckInCommand;
import com.monthlychallenge.application.port.out.CheckInRepository;
import com.monthlychallenge.application.port.out.DaySummaryRepository;
import com.monthlychallenge.application.port.out.UserRepository;
import com.monthlychallenge.domain.model.*;
import com.monthlychallenge.domain.service.DaySummaryDomainService;
import com.monthlychallenge.infrastructure.exception.ResourceNotFoundException;
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
    private final DaySummaryRepository daySummaryRepository;
    private final UserRepository userRepository;
    private final ChallengeService challengeService;
    private final DaySummaryDomainService daySummaryDomainService;

    public CheckInService(CheckInRepository checkInRepository,
                          DaySummaryRepository daySummaryRepository,
                          UserRepository userRepository,
                          ChallengeService challengeService,
                          DaySummaryDomainService daySummaryDomainService) {
        this.checkInRepository = checkInRepository;
        this.daySummaryRepository = daySummaryRepository;
        this.userRepository = userRepository;
        this.challengeService = challengeService;
        this.daySummaryDomainService = daySummaryDomainService;
    }

    @Override
    public CheckIn recordCheckIn(UUID userId, RecordCheckInCommand cmd) {
        LocalDate today = LocalDate.now();
        CheckIn checkIn = checkInRepository
                .findByUserIdAndChallengeIdAndDate(userId, cmd.challengeId(), today)
                .map(existing -> existing
                        .withStatus(cmd.status())
                        .withActualValue(cmd.actualValue())
                        .withUpdatedAt(Instant.now()))
                .orElseGet(() -> CheckIn.builder()
                        .id(UUID.randomUUID()).userId(userId)
                        .challengeId(cmd.challengeId()).date(today)
                        .status(cmd.status()).actualValue(cmd.actualValue())
                        .createdAt(Instant.now()).updatedAt(Instant.now())
                        .build());
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
        return daySummaryDomainService.computeDaySummary(
                userId, date, checkIns, totalDue, user.getMinimumDailyTarget());
    }
}
