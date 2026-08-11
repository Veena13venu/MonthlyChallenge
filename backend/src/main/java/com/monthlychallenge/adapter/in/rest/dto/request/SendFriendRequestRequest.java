package com.monthlychallenge.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SendFriendRequestRequest {
    @NotNull private UUID addresseeId;

    public UUID getAddresseeId()       { return addresseeId; }
    public void setAddresseeId(UUID v) { this.addresseeId = v; }
}
