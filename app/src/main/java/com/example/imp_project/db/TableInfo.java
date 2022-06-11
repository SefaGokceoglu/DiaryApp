package com.example.imp_project.db;

import android.provider.BaseColumns;

public class TableInfo {

        public static final class DiaryEntry implements BaseColumns {
            public static final String TABLE_NAME = "diary";

            public static final String COLUMN_ID = "id";
            public static final String COLUMN_TITLE = "title";
            public static final String COLUMN_DESCRIPTION = "description";
            public static final String COLUMN_EMOJI = "emoji";
            public static final String COLUMN_PASSWORD = "password";
            public static final String COLUMN_DATE = "date";
            public static final String COLUMN_URI = "uri";
            public static final String COLUMN_LOCATION = "location";


        }


}
