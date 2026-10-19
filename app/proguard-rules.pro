# ProGuard / R8 rules for Clinical Abbreviation

# 1. 保证泛型签名和注解不被擦除 (非常关键，防止 Gson 反射失败)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontwarn sun.misc.**

# 2. 保留 Gson 所有类与核心成员
-keep class com.google.gson.** { *; }

# 3. 保留数据模型及其所有字段与方法
-keep class com.example.clinical_abbreviation.AbbreviationModel { *; }
-keepclassmembers class com.example.clinical_abbreviation.AbbreviationModel {
    <fields>;
    <methods>;
}

# 4. 保留所有包含 @SerializedName 的字段
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 5. 保留业务类
-keep class com.example.clinical_abbreviation.MainActivity { *; }
-keep class com.example.clinical_abbreviation.AbbreviationAdapter { *; }
-keep class com.example.clinical_abbreviation.AbbreviationAdapter$* { *; }