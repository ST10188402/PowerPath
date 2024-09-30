package com.opsc.powerpath

class Valid
{
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Example: Phone number must be 10 digits long
        val phoneNumberPattern = "^[0-9]{10}$"
        return phoneNumber.matches(phoneNumberPattern.toRegex())
    }

    fun isValidEmail(email: String) : Boolean {
        val emailPattern = """^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.(com|co\.za)$"""

        if (!Regex(emailPattern).matches(email)) {

            return false
        }

        return true
    }
    fun isValidPassword(password: String) : Boolean {
        val passwordPattern = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"""
        if (!Regex(passwordPattern).matches(password)) {

            return false
        }

        return true
    }


fun ValidHeight (input: Int, errorMessage: (String) -> Unit) : Boolean {
        var error = ""

        return try {
            val height = input
            if (height == null || height < 50.0 || height > 300.0) {
                error = "Please enter a valid height between 50 cm and 300 cm."
                errorMessage(error)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            error = "Please enter a valid height."
            errorMessage(error)
            false
        }
    }

    fun ValidWeight (input: Int, errorMessage: (String) -> Unit) : Boolean {
        var error = ""

        return try {
            val weight = input
            if (weight == null || weight <= 0.0 || weight > 500.0) {
                error = "Please enter a valid weight between 1 kg and 500 kg."
                errorMessage(error)
                false
            } else {
                true
            }
        } catch (e: Exception) {
            error = "Please enter a valid weight."
            errorMessage(error)
            false
        }
    }
}