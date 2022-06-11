package com.example.imp_project.model;

import java.util.List;

public class UpdatedModel {

    public static Diary diary;

    public static Diary getDiary() {
        return diary;
    }

    public static void setDiary(Diary diary) {
        UpdatedModel.diary = diary;
    }

    public static List<Diary> diaries;

    public static List<Diary> getDiaries() {
        return diaries;
    }

    public static void setDiaries(List<Diary> diaries) {
        UpdatedModel.diaries = diaries;
    }

}
