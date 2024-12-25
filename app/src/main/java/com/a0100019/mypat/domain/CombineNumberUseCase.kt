package com.a0100019.mypat.domain

interface CombineNumberUseCase {

    suspend operator fun invoke(
        firstNumber: String,
        secondNumber: String,
        operation: String
    ):String

}
