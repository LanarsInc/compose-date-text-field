package com.lanars.compose.datetextfield

enum class Format(val format: String) {
    MMDDYYYY("MM/dd/yyyy"),
    DDMMYYYY("dd/MM/yyyy"),
    YYYYMMDD("yyyy/MM/dd"),
    YYYYDDMM("yyyy/dd/MM")
}