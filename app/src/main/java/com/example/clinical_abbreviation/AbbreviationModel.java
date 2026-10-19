package com.example.clinical_abbreviation;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * 缩略语数据模型类
 */
public class AbbreviationModel {
    @SerializedName("abbreviation")
    private String abbreviation; // 缩写

    @SerializedName("full_form")
    private String full_form;    // 英文全称

    @SerializedName("chinese")
    private String chinese;      // 中文含义

    public AbbreviationModel() {
    }

    public AbbreviationModel(String abbreviation, String full_form, String chinese) {
        this.abbreviation = abbreviation;
        this.full_form = full_form;
        this.chinese = chinese;
    }

    // Getter 方法 (空值安全)
    public String getAbbreviation() { 
        return abbreviation != null ? abbreviation : ""; 
    }

    public String getFullForm() { 
        return full_form != null ? full_form : ""; 
    }

    public String getChinese() { 
        return chinese != null ? chinese : ""; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbbreviationModel that = (AbbreviationModel) o;
        return Objects.equals(getAbbreviation(), that.getAbbreviation()) &&
               Objects.equals(getFullForm(), that.getFullForm()) &&
               Objects.equals(getChinese(), that.getChinese());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAbbreviation(), getFullForm(), getChinese());
    }

    @Override
    public String toString() {
        return getAbbreviation() + " - " + getFullForm() + " (" + getChinese() + ")";
    }
}