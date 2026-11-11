package com.atlasrh.api.service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class WorkingDays {
    private WorkingDays() {}

    public static long count(LocalDate start, LocalDate end) {
        long days = 0;
        LocalDate date = start;

        while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                days++;
            }
            date = date.plusDays(1);
        }

        return days;
    }

    public static long inYear(LocalDate start, LocalDate end, int year) {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);

        if (start.isBefore(firstDay)) {
            start = firstDay;
        }
        if (end.isAfter(lastDay)) {
            end = lastDay;
        }

        return count(start, end);
    }
}
