package at.jku.win.ss15.pjse.backend;


import java.math.BigDecimal;
import java.util.Date;

/**
 * The <code>BudgetChangedListener</code> reacts on events changing a
 * {@link at.jku.win.pjsess15.backend.Category}<code>'s</code> budget.
 */
public interface BudgetChangedListener {
    /**
     * Replaces the old budget's value with the new one.
     * @param time the time of the change
     * @param from the old budget's value
     * @param to the updated budget's value
     */
    void budgetChanged(Date time, BigDecimal from, BigDecimal to);
}
