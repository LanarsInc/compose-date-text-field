# DateTextField for Jetpack Compose

Date text field with on the fly validation built with Jetpack Compose.
<p>
Input is being validated while user is typing it, so it is impossible to enter a wrong value.

Made in [LANARS](https://lanars.com/).

---

# Download
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
    implementation 'com.github.LanarsInc:compose-date-text-field:{latest version}'
}
```

# Preview
User can only enter existing dates

<img src="https://raw.githubusercontent.com/LanarsInc/compose-date-text-field/develop/media/datetextfield-existing-dates.gif" width="300">
  
User can only enter dates that are in the allowed range
  
<img src="https://raw.githubusercontent.com/LanarsInc/compose-date-text-field/develop/media/datetextfield-values-in-range.gif" width="300">
  
Input is being validated even though some fields are not complete

<img src="https://raw.githubusercontent.com/LanarsInc/compose-date-text-field/develop/media/datetextfield-non-complete-fields.gif" width="300">

# Usage

Basic implementation
```kotlin
DateTextField(
    onEditingComplete = {}
)
```
You can set the exact date boundaries or leave it by default, from 1/1/1900 to 12/31/2100. Date format by default will be MM/DD/YYYY
```kotlin
DateTextField(
    // Detect focus changes
    modifier = Modifier.onFocusChanged { Log.d("DateInput", it.toString()) },
    // Set the desired date format
    format = Format.MMDDYYYY,
    // Set min and max date
    minDate = LocalDate.of(2009, 8, 27),
    maxDate = LocalDate.of(2020, 9, 17),
    // Detect value changes
    onValueChange = {
        Log.d(
            "DateInput",
            it.toString()
        )
    },
    // Get LocalDate object when date is entered
    onEditingComplete = { Log.d("DateInput", it.toString()) },
    // Preset date value
    value = LocalDate.of(2020, 12, 12)
    // Apply custom text style to content
    contentTextStyle = TextStyle(fontSize = 25.sp, color = Color.Black),
    // Apply custom text style to hint
    hintTextStyle = TextStyle(fontSize = 25.sp, color = Color.Gray),
    // Apply custom style to cursor
    cursorBrush = SolidColor(Color.Red),
    // Set custom delimiter
    delimiter = '.',
    // Set padding between digits
    padding = DateDigitsPadding(6.dp)
    // You can set field to be readonly
    readOnly = true
)
```
