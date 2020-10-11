package com.example.setapakhouse

import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.util.regex.Matcher
import java.util.regex.Pattern


class ValidatePassword:TextWatcher {

    var lowerCase : MutableLiveData<Int> = MutableLiveData()
    var upperCase : MutableLiveData<Int> = MutableLiveData()
    var digit : MutableLiveData<Int> = MutableLiveData()
    var specialChar : MutableLiveData<Int> = MutableLiveData()


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s != null){
            lowerCase.value = if(s.hasLowerCase()) { 1 } else { 0 }
            upperCase.value = if(s.hasUpperCase()) { 1 } else { 0 }
            digit.value = if(s.hasDigit()) { 1 } else { 0 }
            specialChar.value = if(s.hasSpecialChar()) { 1 } else { 0 }

        }

    }

    private fun CharSequence.hasLowerCase():Boolean{
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase : Matcher = pattern.matcher(this)
        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase():Boolean{
        val pattern: Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase : Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigit():Boolean{
        val pattern: Pattern = Pattern.compile("[0-9]")
        val hasDigit : Matcher = pattern.matcher(this)
        return hasDigit.find()
    }

    private fun CharSequence.hasSpecialChar():Boolean{
        val pattern: Pattern = Pattern.compile("[!@#$%^&*()_=+{}/.<>|\\[\\]~-]")
        val hasSpecialChar : Matcher = pattern.matcher(this)
        return hasSpecialChar.find()
    }

}