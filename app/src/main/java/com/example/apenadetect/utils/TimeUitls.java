package com.example.apenadetect.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUitls {
    // Hàm chuyển đổi milliseconds sang định dạng ngày giờ
    public static String millisToDate(long millis) {
        // Tạo đối tượng Date từ milliseconds
        Date date = new Date(millis);

        // Định dạng ngày giờ
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Trả về chuỗi định dạng
        return sdf.format(date);
    }

    public static Date createDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month , day); // Tháng bắt đầu từ 0
        return calendar.getTime();
    }

    public static String dateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
}
