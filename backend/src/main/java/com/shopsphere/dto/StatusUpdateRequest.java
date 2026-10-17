package com.shopsphere.dto;

import com.shopsphere.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull OrderStatus status) {}
