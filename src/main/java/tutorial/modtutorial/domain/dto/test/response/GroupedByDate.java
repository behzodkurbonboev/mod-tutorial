package tutorial.modtutorial.domain.dto.test.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class GroupedByDate<T> {
    private String date;
    private List<T> data = new ArrayList<>();

    private GroupedByDate(String date, List<T> data) {
        this.date = date;
        this.data = data;
    }

    public static <T> GroupedByDate<T> of(String date, List<T> data) {
        return new GroupedByDate<>(date, data);
    }
}
