package com.monthlychallenge.adapter.in.rest.mapper;

import com.monthlychallenge.application.dto.FriendshipResponse;
import com.monthlychallenge.domain.models.Friendship;
import org.springframework.stereotype.Component;

@Component
public class FriendshipWebMapper {
    public FriendshipResponse toResponse(Friendship f) {
        return new FriendshipResponse(f.getId(), f.getRequesterId(),
                f.getAddresseeId(), f.getStatus(), f.getCreatedAt());
    }
}
