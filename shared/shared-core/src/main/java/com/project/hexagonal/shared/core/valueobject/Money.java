package com.project.hexagonal.shared.core.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public static final Money MONEY_ZERO = new Money(ZERO);
    public static final Money MONEY_HUNDRED = new Money(BigDecimal.valueOf(100));
    public static final Money MONEY_TEN_THOUSAND = new Money(BigDecimal.valueOf(10_000));
    public static final Money MONEY_HUNDRED_THOUSAND = new Money(BigDecimal.valueOf(100_000.00));
    public static final Money MONEY_ONE_MILLION = new Money(BigDecimal.valueOf(1_000_000.00));

    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount) {
        this.amount = scale(amount);
        this.currency = Currency.TRY;
    }

    public Money(BigDecimal amount, Currency currency) {
        this.amount = scale(amount);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public boolean isGreaterThenZero() {
        return this.amount.compareTo(ZERO) > 0;
    }

    public boolean isEqualToZero() {
        return this.amount.compareTo(ZERO) == 0;
    }

    public boolean isGreaterThen(Money input) {
        return this.amount.compareTo(input.getAmount()) > 0;
    }

    public boolean isLowerThen(Money input) {
        return this.amount.compareTo(input.getAmount()) < 0;
    }

    public boolean isLowerThenZero() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public Money add(Money added) {
        return scaleMoney(this.amount.add(added.getAmount()));
    }

    public Money subtract(Money subtracted) {
        return scaleMoney(this.amount.subtract(subtracted.getAmount()));
    }

    public Money multiply(Money multiplier) {
        return scaleMoney(this.amount.multiply(multiplier.getAmount()));
    }

    public Money multiply(Double multiplier) {
        return scaleMoney(this.amount.multiply(new BigDecimal(multiplier)));
    }

    public Money multiply(BigDecimal multiplier) {
        return scaleMoney(this.amount.multiply(multiplier));
    }

    public Money multiply(int multiplier) {
        return scaleMoney(this.amount.multiply(new BigDecimal(multiplier)));
    }

    public Money divide(Money divider) {
        if (divider.isEqualToZero()) throw new ArithmeticException("Divided by zero");
        return scaleMoney(this.amount.divide(divider.getAmount(), SCALE, ROUNDING_MODE));
    }

    public BigDecimal divide(BigDecimal divider) {
        Objects.requireNonNull(divider, "Divider can not be null value!");
        if (divider.compareTo(ZERO) == 0) throw new ArithmeticException("Divided by zero");
        return scale(this.amount.divide(divider, SCALE, ROUNDING_MODE));
    }

    private static Money scaleMoney(BigDecimal input) {
        Objects.requireNonNull(input, "Money can not be null value!");
        return new Money(input.setScale(SCALE, ROUNDING_MODE)); // new Money(new BigDecimal("00.00"))
    }

    private static BigDecimal scale(BigDecimal input) {
        Objects.requireNonNull(input, "Money can not be null value!");
        return input.setScale(SCALE, ROUNDING_MODE); // new BigDecimal("00.00")
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount);
    }
}
