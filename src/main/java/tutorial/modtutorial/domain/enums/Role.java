package tutorial.modtutorial.domain.enums;

import tutorial.modtutorial.domain.entity.Authority;

import java.io.Serializable;


public enum Role implements Serializable {
    ADMIN {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_ADMIN");
        }
    },
    MODERATOR {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_MODERATOR");
        }
    },
    TEST {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_TEST");
        }
    },
    ARTICLE {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_ARTICLE");
        }
    },
    FORUM {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_FORUM");
        }
    },
    USER {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_USER");
        }
    },
    GUEST {
        @Override
        public Authority asAuthority() {
            return new Authority("ROLE_GUEST");
        }
    };

    public abstract Authority asAuthority();
}
