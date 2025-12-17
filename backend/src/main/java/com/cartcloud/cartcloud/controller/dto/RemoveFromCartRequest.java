package com.cartcloud.cartcloud.controller.dto;

import jakarta.validation.constraints.NotNull;

public record RemoveFromCartRequest(
        @NotNull Long userId,
        @NotNull Long cartItemId
) {}
