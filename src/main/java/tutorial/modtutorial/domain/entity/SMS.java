package tutorial.modtutorial.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorial.modtutorial.domain.enums.Template;


@Getter
@Setter
@Entity
@Table(name = "sms")
@NoArgsConstructor
@AllArgsConstructor
public class SMS extends BaseEntity {
    @Column(name = "user_id")
    private String userId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "template")
    @Enumerated(EnumType.STRING)
    private Template template;

    @Column(name = "code")
    private String code;

    public SMS(String userId, Template template, String code) {
        this.userId = userId;
        this.template = template;
        this.code = code;
    }
}
