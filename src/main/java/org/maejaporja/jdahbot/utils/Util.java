package org.maejaporja.jdahbot.utils;

public class Util {
    public static class CommandUtil {
        public static String[] extractMessage(String message, String regex, int limit) {
            return message.split(regex, limit);
        }

        public static boolean isRootCommand(String[] extractedMessage) {
            if (isSingleLengthCommand(extractedMessage)) {
                String command = extractedMessage[0];
                for (String prefix : ApplicationConfig.PREFIXES) {
                    if (prefix.equals(command))
                        return true;
                }
            }
            return false;
        }

        public static boolean isRootPrefixCommand(String[] extractedMessage) {
            // TODO
            throw new UnsupportedOperationException();
        }

        public static boolean isSingleLengthCommand(String[] extractedMessage) {
            return extractedMessage.length == 1;
        }
    }
}
