package tutorial.modtutorial.domain.dto.general.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class Slice<T> {
    private LocalDateTime minDate;
    private List<T> items;
    private boolean last;

    private Slice(List<T> items, LocalDateTime minDate, boolean last) {
        this.minDate = minDate;
        this.items = items;
        this.last = last;
    }

    public static <T> Slice<T> of(List<T> items, LocalDateTime minDate, int limit) {
        return new Slice<>(items, minDate, items.size() < limit);
    }
}
