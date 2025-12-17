package com.cartcloud.cartcloud.controller.dto;

import jakarta.validation.constraints.NotNull;
public record PayOrederRequest (

  @NotNull Long paymentId

){}
