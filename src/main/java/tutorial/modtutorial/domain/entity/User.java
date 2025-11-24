package tutorial.modtutorial.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tutorial.modtutorial.domain.dto.user.ContactDTO;
import tutorial.modtutorial.domain.enums.AccessType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@ToString
@Entity
@Table(name = "app_user")
public class User extends BaseEntity {
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotEmpty
    @Column(name = "username")
    private String username;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @Column(name = "image_url", columnDefinition = "varchar(255) default NULL")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")}
    )
    @BatchSize(size = 25)
    private Set<Authority> authorities = new HashSet<>();

    @Column(name = "is_verified")
    private boolean verified;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean activated;

    @Column(name = "access_type")
    @Enumerated(EnumType.STRING)
    private AccessType accessType;

    @Column(name = "balance")
    private double balance;

    @Column(name = "articles_count")
    private int articlesCount = 0;

    @Column(name = "posts_count")
    private int postsCount = 0;

    @Column(name = "blocks_count")
    private int blocksCount = 0;

    @Column(name = "speciality", columnDefinition = "TEXT")
    @Size(max = 200, message = "Speciality must be at most 200 characters")
    private String speciality;

    @Column(name = "bio", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Bio must be at most 1000 characters")
    private String bio;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<ContactDTO> contacts;

    @Column(name = "version")
    private String version;

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }

        if (firstName != null) {
            return firstName;
        }

        if (lastName != null) {
            return lastName;
        }

        return "Foydalanuvchi";
    }
}
