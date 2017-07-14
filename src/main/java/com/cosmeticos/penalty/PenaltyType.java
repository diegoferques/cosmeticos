package com.cosmeticos.penalty;

/**
 * Created by Lulu on 13/07/2017.
 */
final class PenaltyType
{
    public static final String NONE = "NONE";
    public static final String ABORT_TOLERANCE_EXPIRED = "ABORT_TOLERANCE_EXPIRED";
    public static final String BASIC_USER_ABORTING = "BASIC_USER_ABORTING";
    public static final String PREMIUM_USER_ABORTING = "PREMIUM_USER_ABORTING";

    enum Value {
        NONE(PenaltyType.NONE),
        ABORT_TOLERANCE_EXPIRED(PenaltyType.ABORT_TOLERANCE_EXPIRED),
        BASIC_USER_ABORTING(PenaltyType.BASIC_USER_ABORTING),
        PREMIUM_USER_ABORTING(PenaltyType.PREMIUM_USER_ABORTING);

        private final String value;

        Value(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}