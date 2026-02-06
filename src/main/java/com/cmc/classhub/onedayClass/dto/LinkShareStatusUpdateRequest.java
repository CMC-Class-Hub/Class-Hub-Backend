package com.cmc.classhub.onedayClass.dto;

import com.cmc.classhub.onedayClass.domain.LinkShareStatus;
import jakarta.validation.constraints.NotNull;

public record LinkShareStatusUpdateRequest(
        @NotNull LinkShareStatus linkShareStatus
) {}
