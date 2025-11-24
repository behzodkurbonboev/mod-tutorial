package tutorial.modtutorial.domain.enums;

public enum Template {
    REGISTRATION {
        @Override
        public String withCode(String code) {
            return "Kodni hech kimga bermang! TTT mobil ilovasiga ro'yxatdan o'tishni tasdiqlash uchun kod: " + code;
        }

        @Override
        public int getCodeLength() {
            return 5;
        }
    },
    FORGOT_PASSWORD {
        @Override
        public String withCode(String code) {
            return "Kodni hech kimga bermang! TTT mobil ilovasida parolni yangilashni tasdiqlash uchun kod: " + code;
        }

        @Override
        public int getCodeLength() {
            return 5;
        }
    },
    DELETE_ACCOUNT {
        @Override
        public String withCode(String code) {
            return "Kodni hech kimga bermang! TTT mobil ilovasidan akkauntni o'chirishni tasdiqlash uchun kod: " + code;
        }

        @Override
        public int getCodeLength() {
            return 10;
        }
    };

    public abstract String withCode(String code);
    public abstract int getCodeLength();
}
