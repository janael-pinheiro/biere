package com.biere.catalog.containers.api.dtos

import lombok.Getter
import lombok.Setter
import org.jetbrains.annotations.NotNull

@Getter
@Setter
data class CountryRegistrationDTO(@NotNull val name: String)