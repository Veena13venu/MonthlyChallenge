package com.monthlychallenge.domain.models;

import java.util.UUID;

import com.monthlychallenge.domain.enums.ChallengeCategory;
import com.monthlychallenge.domain.enums.ChallengeFrequency;

public final class ChallengeTemplate {

    private final UUID id;
    private final String title;
    private final String description;
    private final ChallengeCategory category;
    private final ChallengeFrequency suggestedFrequency;
    private final ChallengeTarget suggestedTarget;

    private ChallengeTemplate(Builder b) {
        this.id = b.id;
        this.title = b.title;
        this.description = b.description;
        this.category = b.category;
        this.suggestedFrequency = b.suggestedFrequency;
        this.suggestedTarget = b.suggestedTarget;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ChallengeCategory getCategory() {
        return category;
    }

    public ChallengeFrequency getSuggestedFrequency() {
        return suggestedFrequency;
    }

    public ChallengeTarget getSuggestedTarget() {
        return suggestedTarget;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private String title;
        private String description;
        private ChallengeCategory category;
        private ChallengeFrequency suggestedFrequency;
        private ChallengeTarget suggestedTarget;

        public Builder id(UUID v) {
            this.id = v;
            return this;
        }

        public Builder title(String v) {
            this.title = v;
            return this;
        }

        public Builder description(String v) {
            this.description = v;
            return this;
        }

        public Builder category(ChallengeCategory v) {
            this.category = v;
            return this;
        }

        public Builder suggestedFrequency(ChallengeFrequency v) {
            this.suggestedFrequency = v;
            return this;
        }

        public Builder suggestedTarget(ChallengeTarget v) {
            this.suggestedTarget = v;
            return this;
        }

        public ChallengeTemplate build() {
            return new ChallengeTemplate(this);
        }
    }
}
