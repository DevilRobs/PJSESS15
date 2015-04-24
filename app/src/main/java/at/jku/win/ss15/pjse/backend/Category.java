package at.jku.win.ss15.pjse.backend;


import java.math.BigDecimal;

/**
 * A {@code Category} defines a section and a currency on which expenditures can be spent on.
 */
public class Category {
    private final String name;
    private BigDecimal budget;
    private final Currency currency;

    /**
     * Creates a new category to be used within the data base.
     * Both parameters must not be null, as requested by the database
     * and cannot be modified.
     *
     * @param name     A {@link String} defining the category's name
     * @param currency A specified {@link Currency}
     */
    public Category(String name, Currency currency) {
        if (name == null)
            throw new NullPointerException("name must not be NULL");
        this.name = name;
        if (currency == null)
            throw new NullPointerException("currency must not be NULL");
        this.currency = currency;
    }

    /**
     * @return the category's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the budget for this category.
     */
    public BigDecimal getBudget() {
        return budget;
    }

    /**
     * @param budget a {@link java.math.BigDecimal} object representing the category's budget.
     */
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    /**
     * @return the specified currency for this category.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param o the object the current category should be compared to.
     * @return {@code true}, if the other object equals this category. <br>
     * {@code false}, if the other object does not equal this category.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!name.equals(category.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 11 * name.hashCode();
    }

    @Override
    public String toString() {
        return name + " [" + budget + " " + currency + "]";
    }
}
