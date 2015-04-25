package at.jku.win.ss15.pjse.backend;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The <code>BudgetChangedListener</code> reacts on events changing a
 * {@link at.jku.win.ss15.pjse.backend.Category}<code>'s</code> budget.
 */
public interface BudgetChangedListener {
    /**
     * Replaces the old budget's value with the new one.
     *
     * @param c    the name of the category
     * @param time the time of the change
     * @param from the old budget's value
     * @param to   the updated budget's value
     */
    void budgetChanged(String c, Date time, BigDecimal from, BigDecimal to);

    List<LogItem> getAllChanges() throws UnsupportedOperationException;

    public static class LogItem implements Serializable {
        private Date date;
        private String categoryName;
        private BigDecimal bugget;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public BigDecimal getBugget() {
            return bugget;
        }

        public void setBugget(BigDecimal bugget) {
            this.bugget = bugget;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LogItem logItem = (LogItem) o;

            if (bugget != null ? !bugget.equals(logItem.bugget) : logItem.bugget != null)
                return false;
            if (categoryName != null ? !categoryName.equals(logItem.categoryName) : logItem.categoryName != null)
                return false;
            if (date != null ? !date.equals(logItem.date) : logItem.date != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = date != null ? date.hashCode() : 0;
            result = 31 * result + (categoryName != null ? categoryName.hashCode() : 0);
            result = 31 * result + (bugget != null ? bugget.hashCode() : 0);
            return result;
        }
    }
}
