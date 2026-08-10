package com.monthlychallenge.infrastructure.web.mapper;

import com.monthlychallenge.domain.model.Friendship;
import com.monthlychallenge.infrastructure.web.dto.response.FriendshipResponse;
import org.springframework.stereotype.Component;

@Component
public class FriendshipWebMapper {
    public FriendshipResponse toResponse(Friendship f) {
        return new FriendshipResponse(f.getId(), f.getRequesterId(),
                f.getAddresseeId(), f.getStatus(), f.getCreatedAt());
    }
}
