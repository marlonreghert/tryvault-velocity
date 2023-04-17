package com.tryvault.constants;

import java.math.BigDecimal;

public final class LoadFundsRequestLimits {
    public static final BigDecimal AMOUNT_PER_DAY = new BigDecimal("5000");
    public static final BigDecimal AMOUNT_PER_WEEK = new BigDecimal("20000");

    public static final int LOADS_PER_DAY = 3;
}
