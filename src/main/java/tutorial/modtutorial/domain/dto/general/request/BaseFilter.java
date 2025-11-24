package tutorial.modtutorial.domain.dto.general.request;

import tutorial.modtutorial.constant.Date;

import java.time.LocalDateTime;


public class BaseFilter {
    private LocalDateTime minDate;

    public BaseFilter() {
    }

    public LocalDateTime getMinDate() {
        return minDate != null ? minDate : Date.MAX_DATE;
    }

    public void setMinDate(LocalDateTime minDate) {
        this.minDate = minDate;
    }
}
