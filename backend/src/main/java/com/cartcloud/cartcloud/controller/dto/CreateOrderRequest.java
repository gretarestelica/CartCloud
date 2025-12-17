package com.cartcloud.cartcloud.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest (

  @NotNull Long userID,
  @NotNull String paymentMethod


){}
