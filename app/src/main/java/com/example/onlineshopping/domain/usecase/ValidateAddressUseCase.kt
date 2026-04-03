package com.example.onlineshopping.domain.usecase

import javax.inject.Inject

class ValidateAddressUseCase@Inject constructor() {
    operator fun invoke(line1: String, city: String, postcode: String): Result<Unit> {
        return if (line1.isBlank() || city.isBlank() || postcode.isBlank()) {
            Result.failure(Exception("Please fill in your delivery address"))
        } else {
            Result.success(Unit)
        }
    }
}