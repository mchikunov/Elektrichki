package com.me.elektrichki;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coordination")
public class CodeNameCoordination {


    @PrimaryKey(autoGenerate = true)
    public int coordId;

    private String codeE;
    private String nameE;

    public CodeNameCoordination(String codeE, String nameE) {
        this.codeE = codeE;
        this.nameE = nameE;
    }

    public String getCodeE() {
        return codeE;
    }

    public void setCodeE(String codeE) {
        this.codeE = codeE;
    }

    public String getNameE() {
        return nameE;
    }

    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    @Override
    public String toString() {
        return "CodeNameCoordination{" +
                "codeE='" + codeE + '\'' +
                ", nameE='" + nameE + '\'' +
                '}';
    }
}
