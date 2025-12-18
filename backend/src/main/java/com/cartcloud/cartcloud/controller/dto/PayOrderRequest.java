package com.cartcloud.cartcloud.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record PayOrderRequest (

  @NotNull Long orderId,

  @NotBlank String method

){}
