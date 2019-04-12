package com.example.waitlist.Data;

import android.provider.BaseColumns;

public class WaitListCounteract
{

    public static final class WaitListEntry implements BaseColumns {

        public static final String TABLE_NAME = "waitList";
        public static final String COLUMNS_GUEST_NAME = "guestName";
        public static final String COLUMNS_PARTY_SIZE = "partySize";
        public static final String COLUMNS_TIMESTAMP = "timeStamp";

    }
}
