package org.maejaporja.jdahbot.utils;

public class TimeFormat {

        public static String getTimestamp(long milliseconds)
        {
            int seconds = (int) (milliseconds / 1000) % 60 ;
            int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
            int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

            if (hours > 0)
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            else
                return String.format("%02d:%02d", minutes, seconds);
        }

}
