package com.shopping.microservices.common_library.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations.
 * 
 * Provides static methods for formatting, parsing, and comparing
 * Java 8+ date/time objects with ISO format support.
 */
public final class DateTimeUtil {

    /**
     * Default timezone (Vietnam)
     */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /**
     * ISO datetime formatter with milliseconds
     */
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * ISO date formatter
     */
    public static final DateTimeFormatter ISO_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * ISO time formatter
     */
    public static final DateTimeFormatter ISO_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Display datetime formatter (Vietnamese format)
     */
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Display date formatter (Vietnamese format)
     */
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Private constructor to prevent instantiation
    private DateTimeUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get current timestamp.
     * 
     * @return Current LocalDateTime
     */
    public static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(DEFAULT_ZONE);
    }

    /**
     * Get current date.
     * 
     * @return Current LocalDate
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now(DEFAULT_ZONE);
    }

    /**
     * Get current time.
     * 
     * @return Current LocalTime
     */
    public static LocalTime getCurrentTime() {
        return LocalTime.now(DEFAULT_ZONE);
    }

    /**
     * Format LocalDateTime to ISO string.
     * 
     * @param dateTime LocalDateTime to format
     * @return ISO formatted string (yyyy-MM-dd'T'HH:mm:ss.SSS)
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_DATETIME_FORMATTER);
    }

    /**
     * Format LocalDateTime to display string.
     * 
     * @param dateTime LocalDateTime to format
     * @return Display formatted string (dd/MM/yyyy HH:mm:ss)
     */
    public static String formatDateTimeForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DISPLAY_DATETIME_FORMATTER);
    }

    /**
     * Format LocalDate to ISO string.
     * 
     * @param date LocalDate to format
     * @return ISO formatted string (yyyy-MM-dd)
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(ISO_DATE_FORMATTER);
    }

    /**
     * Format LocalDate to display string.
     * 
     * @param date LocalDate to format
     * @return Display formatted string (dd/MM/yyyy)
     */
    public static String formatDateForDisplay(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DISPLAY_DATE_FORMATTER);
    }

    /**
     * Parse ISO datetime string to LocalDateTime.
     * 
     * @param dateTime ISO formatted string
     * @return LocalDateTime or null if parsing fails
     */
    public static LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime, ISO_DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            // Try standard ISO format as fallback
            try {
                return LocalDateTime.parse(dateTime);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    /**
     * Parse ISO date string to LocalDate.
     * 
     * @param date ISO formatted string
     * @return LocalDate or null if parsing fails
     */
    public static LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(date, ISO_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Check if a datetime has expired.
     * 
     * @param expiryTime Time to check
     * @return true if expired, false otherwise
     */
    public static boolean isExpired(LocalDateTime expiryTime) {
        if (expiryTime == null) {
            return true;
        }
        return expiryTime.isBefore(getCurrentTimestamp());
    }

    /**
     * Check if a datetime is in the future.
     * 
     * @param dateTime Time to check
     * @return true if in the future, false otherwise
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(getCurrentTimestamp());
    }

    /**
     * Check if a datetime is in the past.
     * 
     * @param dateTime Time to check
     * @return true if in the past, false otherwise
     */
    public static boolean isPast(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(getCurrentTimestamp());
    }

    /**
     * Calculate duration between two datetimes in minutes.
     * 
     * @param start Start datetime
     * @param end End datetime
     * @return Duration in minutes
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Calculate duration between two datetimes in hours.
     * 
     * @param start Start datetime
     * @param end End datetime
     * @return Duration in hours
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Calculate duration between two dates in days.
     * 
     * @param start Start date
     * @param end End date
     * @return Duration in days
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Get start of day (00:00:00.000).
     * 
     * @param dateTime DateTime to get start of day for
     * @return Start of day
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Get end of day (23:59:59.999999999).
     * 
     * @param dateTime DateTime to get end of day for
     * @return End of day
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Add minutes to a datetime.
     * 
     * @param dateTime Base datetime
     * @param minutes Minutes to add
     * @return New datetime
     */
    public static LocalDateTime addMinutes(LocalDateTime dateTime, long minutes) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Add hours to a datetime.
     * 
     * @param dateTime Base datetime
     * @param hours Hours to add
     * @return New datetime
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusHours(hours);
    }

    /**
     * Add days to a datetime.
     * 
     * @param dateTime Base datetime
     * @param days Days to add
     * @return New datetime
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }

    /**
     * Check if two datetimes are on the same day.
     * 
     * @param dt1 First datetime
     * @param dt2 Second datetime
     * @return true if same day, false otherwise
     */
    public static boolean isSameDay(LocalDateTime dt1, LocalDateTime dt2) {
        if (dt1 == null || dt2 == null) {
            return false;
        }
        return dt1.toLocalDate().equals(dt2.toLocalDate());
    }
}
