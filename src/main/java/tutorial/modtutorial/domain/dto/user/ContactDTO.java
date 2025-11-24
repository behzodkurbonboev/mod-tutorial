package tutorial.modtutorial.domain.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tutorial.modtutorial.constant.ContactTypes.FACEBOOK;
import static tutorial.modtutorial.constant.ContactTypes.INSTAGRAM;
import static tutorial.modtutorial.constant.ContactTypes.LINKED_IN;
import static tutorial.modtutorial.constant.ContactTypes.PHONE;
import static tutorial.modtutorial.constant.ContactTypes.TELEGRAM;
import static tutorial.modtutorial.constant.ContactTypes.WEB;
import static tutorial.modtutorial.constant.ContactTypes.YOU_TUBE;


@Getter
@Setter
public class ContactDTO implements Serializable {
    private String type;
    private String name;
    private String link;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+998\\d{9}$");
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^(?:https?://)?(?:www\\.)?([^/]+)");


    public ContactDTO() {
    }

    public ContactDTO fill() {
        type = getContactType();

        if (name == null || name.isBlank()) {
            name = type;
        }

        return this;
    }


    private String getContactType() {
        if (PHONE_PATTERN.matcher(link).matches()) {
            return PHONE;
        }

        String domain = link;

        Matcher matcher = DOMAIN_PATTERN.matcher(link);
        if (matcher.find()) {
            domain = matcher.group(1);
        }

        if ("t.me".equals(domain) || "telegram.org".equals(domain)) {
            return TELEGRAM;
        }

        if ("facebook.com".equals(domain)) {
            return FACEBOOK;
        }

        if ("youtube.com".equals(domain) || "youtu.be".equals(domain)) {
            return YOU_TUBE;
        }

        if ("instagram.com".equals(domain)) {
            return INSTAGRAM;
        }

        if ("linkedin.com".equals(domain)) {
            return LINKED_IN;
        }

        return WEB;
    }
}
