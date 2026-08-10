package com.monthlychallenge.infrastructure.web.dto.request;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
    @Size(max = 100) private String displayName;
    @Size(max = 500) private String profilePhotoUrl;

    public String getDisplayName()          { return displayName; }
    public void setDisplayName(String v)    { this.displayName = v; }
    public String getProfilePhotoUrl()      { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String v){ this.profilePhotoUrl = v; }
}
