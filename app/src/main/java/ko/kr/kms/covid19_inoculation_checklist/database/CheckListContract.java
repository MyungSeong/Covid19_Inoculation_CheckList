package ko.kr.kms.covid19_inoculation_checklist.database;

import android.provider.BaseColumns;

public final class CheckListContract {

    private CheckListContract() {}

    public static class CheckListEntry implements BaseColumns {

        public static final String UNCONFIRMED_TABLE = "unconfirmedList";
        public static final String CONFIRMED_TABLE = "confirmedList";

        public static final String RESERVATION_DATE = "reservationDate";
        public static final String RESERVATION_TIME = "reservationTime";
        public static final String INOCULATED = "inoculated";
        public static final String SUBJECT = "subject";
        public static final String NAME = "name";
        public static final String REGISTRATION_NUMBER = "registrationNumber";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String FACILITY_NAME = "facilityName";
    }
}
