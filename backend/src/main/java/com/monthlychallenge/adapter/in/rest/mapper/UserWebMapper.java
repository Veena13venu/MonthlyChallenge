package com.monthlychallenge.adapter.in.rest.mapper;

import com.monthlychallenge.application.dto.UserResponse;
import com.monthlychallenge.domain.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {
    public UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getDisplayName(), u.getEmail(),
                u.getProfilePhotoUrl(),
                u.getMinimumDailyTarget().getValue(),
                u.getMinimumDailyTarget().isPercentage());
    }
}
