package org.upsmf.grievance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.upsmf.grievance.constants.Constants;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final String ENCOUNTERED_AN_EXCEPTION = "Encountered an Exception : %s";

    public static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String INVOICE_DATE_FORMATTER = "EEEE, MMMM d, yyyy";
    public static final String YYYYMMDD_FORMATTER = "yyyy-MM-dd";
    public static final String DATE_MONTH_YEAR_FORMAT = "dd-MMMM-yyyy";

    /**
     * this method take date object and format the date with time zone IST.
     *
     * @param date
     *            date object
     * @return formatted date as String in "yyyy-MM-dd HH:mm:ss"
     */
    public static String getFormattedDateInIST(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        return format.format(date);
    }


    public static String getFormattedDateInString(Timestamp ts) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        return dateTimeFormatter.format(ts.toLocalDateTime());
    }

    /**
     * this method take date object and time zone ,and format date object with
     * incoming time zone.
     *
     * @param date
     *            date object
     * @param timeZone
     * @return formatted date as String in "yyyy-MM-dd HH:mm:ss"
     */
    public static String getFormattedDateWithTimeZone(Date date, String timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        return format.format(date);
    }

    /**
     * this method will format current date object with incoming formatter
     *
     * @param format
     *            date formatter
     * @return String formatted date object.
     */
    public static String getCurrentDate(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    /**
     * this method will format long time value to given time zone with MM-dd-yyyy
     * HH:mm:ss
     *
     * @param timeZone
     * @param time
     * @return String
     */
    public static String convertLongToStringAsDateTime(String timeZone, long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * this method will format long time value to given time zone with MM-dd-yyyy
     * HH:mm:ss
     *
     * @param timeZone
     * @param date
     * @param incomingDateFormat
     * @return String
     */
    public static String dateFormatter(String timeZone, String date, String incomingDateFormat) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        SimpleDateFormat incomingFormatter = new SimpleDateFormat(incomingDateFormat);
        incomingFormatter.setTimeZone(TimeZone.getDefault());
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String response = "";
        try {
            Date defaultFormattedDate = incomingFormatter.parse(date);
            response = format.format(defaultFormattedDate);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return response;
    } //

    /**
     * this method will format long time value to given time zone with MMMMM d
     * EEEEEEEEE,hh:mm a
     *
     * @param timeZone
     * @param date
     * @param incomingDateFormat
     * @return String
     */
    public static String ormatterInMMMMdEEE(String timeZone, String date, String incomingDateFormat) {
        SimpleDateFormat format = new SimpleDateFormat("EEEEEEEEE, MMMMM d @hh:mm a");
        SimpleDateFormat incomingFormatter = new SimpleDateFormat(incomingDateFormat);
        incomingFormatter.setTimeZone(TimeZone.getDefault());
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        String response = "";
        try {
            Date defaultFormattedDate = incomingFormatter.parse(date);
            response = format.format(defaultFormattedDate);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return response;
    }

    /**
     * this method will format long time value to given time zone with MMMMM d
     * EEEEEEEEE,hh:mm a
     *
     * @param timeZone
     * @param time
     * @return String
     */
    public static String convertLongToStringAsMMMMEEEE(String timeZone, long time) {
        SimpleDateFormat format = new SimpleDateFormat("EEEEEEEEE, MMMMM d @hh:mm a");
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * this method will format date object with time zone
     *
     * @param date
     *            date object
     * @param timeZone
     *            String
     * @return String
     */
    public static String getSqlTimeStamp(Date date, String... timeZone) {

        DateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        if (timeZone != null && timeZone.length > 0) {
            format.setTimeZone(TimeZone.getTimeZone(timeZone[0]));
        }
        return format.format(date);
    }

    /**
     * this method will convert long value to date object and provide formatted date
     * object in "yyyy-MM-dd HH:mm:ss" this form
     *
     * @param time
     * @return String
     */
    public static String getSqlTimeStamp(Long time) {
        return getSqlTimeStamp(new Date(time));
    }

    /**
     *
     * @param date
     * @param timeZone
     * @return Date
     * @throws Exception
     */
    public static Date getDateInDefaultTimeZone(String date, String timeZone) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat formatterWithDefaultTimeZone = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));
        Date reservationTimeWithTimeZone = format.parse(date + ":00");
        String reservationTimeWithDefaultTimeZone = formatterWithDefaultTimeZone.format(reservationTimeWithTimeZone);
        return formatterWithDefaultTimeZone.parse(reservationTimeWithDefaultTimeZone);
    }

    /**
     *
     * @return Date
     * @throws Exception
     */
    public static Date getCurrentDate() throws Exception {
        SimpleDateFormat formatterWithDefaultTimeZone = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date currDate = new Date();
        String currTimeWithTimeZone = formatterWithDefaultTimeZone.format(currDate);
        return formatterWithDefaultTimeZone.parse(currTimeWithTimeZone);
    }

    /**
     * This method will convert String date time with IST time Zone in "yyyy-MM-dd
     * HH:mm:ss" format
     *
     * @param date
     *            String
     * @return Date jave.util Date object
     */
    public static Date convertStringToDateWithTime(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        Date afterFormat = null;
        try {
            afterFormat = format.parse(date);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return afterFormat;
    }

    /**
     * this method will provide current data in GMT.
     *
     * @return Date Object
     */
    public static Date getCurrentDateInGmt() {
        Calendar c = Calendar.getInstance();
        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();
        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMints = offset / 1000 / 60 % 60;
        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMints));
        return c.getTime();
    }

    /**
     * this method is used to take system current time and return time with time
     * zone.
     *
     * @param date
     *            current date
     * @param timezone
     *            time
     * @return String
     */
    public static String convertDateWithTimeZone(Date date, String timezone) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormatGmt.format(date);

    }

    /**
     * this method will convert String to date object with system time zone.
     *
     * @param date
     *            String
     * @return Date
     */
    public static Date convertStringToDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        Date afterFormat = null;
        try {
            afterFormat = format.parse(date);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return afterFormat;
    }

    /**
     * this will format incoming date with MM/dd/yyyy
     *
     * @param date
     *            String
     * @return String
     */
    public static String formateWithMMddyyyy(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = "";
        try {
            formattedDate = format.format(dateFormat.parse(date));
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }

        return formattedDate;
    }

    /**
     * This method will return reservation time and current time difference in
     * minutes.
     *
     * @param date
     *            String reservation time
     * @return int
     */
    public static int getDateDifferenceInMinutes(String date) {
        int diffDate = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        try {
            Date requestedDate = dateFormat.parse(date);
            Date currentDate = new Date();
            currentDate = dateFormat.parse(dateFormat.format(currentDate));
            diffDate = (int) ((requestedDate.getTime() - currentDate.getTime()) / 60000);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return diffDate;
    }

    /**
     * This method will formate incoming String to time zone and format.
     *
     * @param date
     * @param timZone
     * @param format
     * @return
     */
    public static String formatStringToTimeZone(String date, String timZone, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timZone));
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(format);
        dateFormat1.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        String formattedDate = date;
        try {
            Date parseDate = dateFormat.parse(date);
            formattedDate = dateFormat1.format(parseDate);
            formattedDate = dateFormat1.format(dateFormat1.parse(formattedDate));
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return formattedDate;
    }

    /**
     * This method will provide current date in IST format in YYYY_MM_DD format.
     *
     * @return String
     */
    public static String getYyyyMmDdInIST() {
        SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD_FORMATTER);
        format.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        return format.format(new Date());
    }

    /**
     * this method will convert String to date object in IST
     *
     * @param date
     *            String
     * @return Date
     */
    public static Date convertStringToDateIST(String date) {
        SimpleDateFormat format = new SimpleDateFormat(YYYYMMDD_FORMATTER);
        format.setTimeZone(TimeZone.getTimeZone(Constants.TIME_ZONE));
        Date afterFormat = null;
        try {
            afterFormat = format.parse(date);
        } catch (Exception e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return afterFormat;
    }

    public static String getTimeWithHHMMSSFormat() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());

    }

    public static Date convertPorjectUploadDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_MONTH_YEAR_FORMAT);
        Date formatDate = null;
        try {
            formatDate = format.parse(date);
        } catch (ParseException e) {
            LOGGER.error(String.format(ENCOUNTERED_AN_EXCEPTION, e.getMessage()));
        }
        return formatDate;
    }
}
