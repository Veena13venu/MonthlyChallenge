package com.monthlychallenge.infrastructure.web.mapper;

import com.monthlychallenge.domain.model.User;
import com.monthlychallenge.infrastructure.web.dto.response.UserResponse;
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
