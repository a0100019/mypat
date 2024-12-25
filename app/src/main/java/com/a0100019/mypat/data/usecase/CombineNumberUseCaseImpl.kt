package com.a0100019.mypat.data.usecase

import com.a0100019.mypat.domain.CombineNumberUseCase
import javax.inject.Inject

class CombineNumberUseCaseImpl @Inject constructor(

) : CombineNumberUseCase {
    override suspend fun invoke(firstNumber: String, secondNumber: String, operation:String): String {
        try {
            val intFirstNumber: Int = firstNumber.toInt()
            val intSecondNumber: Int = secondNumber.toInt()
            return when (operation) {
                "+" -> (intFirstNumber + intSecondNumber).toString()
                "-" -> (intFirstNumber - intSecondNumber).toString()
                "/" -> (intFirstNumber / intSecondNumber).toString()
                "*" -> (intFirstNumber * intSecondNumber).toString()
                else -> "부호를 입력하세요"
            }
//            val result = intFirstNumber  intSecondNumber
//            return result.toString()
        } catch (_: Exception) {
            return "숫자를 입력하세요"
        }
    }
}