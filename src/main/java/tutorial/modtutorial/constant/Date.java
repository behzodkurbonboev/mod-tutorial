package tutorial.modtutorial.constant;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public interface Date {
    DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm/dd.MM.yyyy");
    LocalDateTime MIN_DATE = LocalDateTime.of(2010, 1, 1, 1, 1);
    LocalDateTime MAX_DATE = LocalDateTime.of(2100, 1, 1, 1, 1);

    long CURRENT_YEAR = 2025; // should be updated on every year
    String[] MONTHS = {
            "Yanvar",
            "Fevral",
            "Mart",
            "Aprel",
            "May",
            "Iyun",
            "Iyul",
            "Avgust",
            "Sentabr",
            "Oktabr",
            "Noyabr",
            "Dekabr"
    };

    static String toDate(LocalDateTime date) {
        if (date.getYear() == CURRENT_YEAR) {
            return MONTHS[date.getMonthValue() - 1] + " " + date.getDayOfMonth();
        }

        return MONTHS[date.getMonthValue() - 1] + " " + date.getDayOfMonth() + ", " + date.getYear();
    }
}
