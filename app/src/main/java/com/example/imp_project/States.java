package com.example.imp_project;

import com.example.imp_project.model.Diary;

import java.util.List;

public class States {

    public static List<Diary> diaryList;

    public static String toPhoneNumber;

    public static Diary sendDiary;

    public static Diary getSendDiary() {
        return sendDiary;
    }

    public static void setSendDiary(Diary sendDiary) {
        States.sendDiary = sendDiary;
    }

    public static List<Diary> getDiaryList() {
        return diaryList;
    }

    public static void setDiaryList(List<Diary> diaryList) {
        States.diaryList = diaryList;
    }

    public static String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public static void setToPhoneNumber(String toPhoneNumber) {
        States.toPhoneNumber = toPhoneNumber;
    }
}
