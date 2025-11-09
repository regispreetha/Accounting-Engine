package com.accounting.reconciliation.engine;

import com.accounting.reconciliation.model.LedgerTransaction;
import com.accounting.reconciliation.model.ReconciliationRule;
import com.accounting.reconciliation.model.RuleMatchingCriteria;
import com.accounting.reconciliation.model.SubledgerTransaction;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Rules Engine for evaluating matching criteria
 */
@Component
public class RulesEngine {

    /**
     * Evaluate if a ledger transaction matches a subledger transaction based on the rule
     */
    public boolean evaluateMatch(
        LedgerTransaction ledgerTrans,
        SubledgerTransaction subledgerTrans,
        ReconciliationRule rule
    ) {
        try {
            List<RuleMatchingCriteria> criteriaList = rule.getMatchingCriteria();
            if (criteriaList == null || criteriaList.isEmpty()) {
                return false;
            }

            boolean result = true;
            String currentLogicalOperator = "AND";

            for (RuleMatchingCriteria criteria : criteriaList) {
                boolean criteriaMatch = evaluateCriteria(ledgerTrans, subledgerTrans, criteria, rule);

                if ("AND".equals(currentLogicalOperator)) {
                    result = result && criteriaMatch;
                } else if ("OR".equals(currentLogicalOperator)) {
                    result = result || criteriaMatch;
                }

                // Update logical operator for next iteration
                if (criteria.getLogicalOperator() != null) {
                    currentLogicalOperator = criteria.getLogicalOperator();
                }

                // Short-circuit for AND operations
                if (!result && "AND".equals(currentLogicalOperator)) {
                    return false;
                }
            }

            return result;
        } catch (Exception e) {
            // Log error
            return false;
        }
    }

    /**
     * Evaluate a single matching criteria
     */
    private boolean evaluateCriteria(
        LedgerTransaction ledgerTrans,
        SubledgerTransaction subledgerTrans,
        RuleMatchingCriteria criteria,
        ReconciliationRule rule
    ) {
        try {
            Object ledgerValue = getFieldValue(ledgerTrans, criteria.getFieldName());
            Object subledgerValue = getFieldValue(subledgerTrans,
                criteria.getComparisonField() != null ? criteria.getComparisonField() : criteria.getFieldName());

            String operator = criteria.getOperator();

            switch (operator) {
                case "EQUALS":
                    return evaluateEquals(ledgerValue, subledgerValue, rule);
                case "NOT_EQUALS":
                    return !evaluateEquals(ledgerValue, subledgerValue, rule);
                case "GREATER_THAN":
                    return compareNumeric(ledgerValue, subledgerValue) > 0;
                case "LESS_THAN":
                    return compareNumeric(ledgerValue, subledgerValue) < 0;
                case "GREATER_EQUAL":
                    return compareNumeric(ledgerValue, subledgerValue) >= 0;
                case "LESS_EQUAL":
                    return compareNumeric(ledgerValue, subledgerValue) <= 0;
                case "CONTAINS":
                    return ledgerValue != null && subledgerValue != null &&
                           ledgerValue.toString().contains(subledgerValue.toString());
                case "STARTS_WITH":
                    return ledgerValue != null && subledgerValue != null &&
                           ledgerValue.toString().startsWith(subledgerValue.toString());
                case "ENDS_WITH":
                    return ledgerValue != null && subledgerValue != null &&
                           ledgerValue.toString().endsWith(subledgerValue.toString());
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Evaluate equals with tolerance support
     */
    private boolean evaluateEquals(Object ledgerValue, Object subledgerValue, ReconciliationRule rule) {
        if (ledgerValue == null || subledgerValue == null) {
            return ledgerValue == subledgerValue;
        }

        // For numeric values, consider tolerance
        if (ledgerValue instanceof BigDecimal && subledgerValue instanceof BigDecimal) {
            BigDecimal ledgerAmount = (BigDecimal) ledgerValue;
            BigDecimal subledgerAmount = (BigDecimal) subledgerValue;

            if ("EXACT".equals(rule.getMatchingType())) {
                return ledgerAmount.compareTo(subledgerAmount) == 0;
            } else if ("TOLERANCE".equals(rule.getMatchingType())) {
                BigDecimal difference = ledgerAmount.subtract(subledgerAmount).abs();

                if (rule.getToleranceAmount() != null &&
                    difference.compareTo(rule.getToleranceAmount()) <= 0) {
                    return true;
                }

                if (rule.getTolerancePercentage() != null) {
                    BigDecimal percentageDiff = difference.divide(ledgerAmount.abs(), 4, BigDecimal.ROUND_HALF_UP)
                                                          .multiply(new BigDecimal("100"));
                    return percentageDiff.compareTo(rule.getTolerancePercentage()) <= 0;
                }

                return difference.compareTo(BigDecimal.ZERO) == 0;
            }
        }

        // For other types, use standard equals
        return ledgerValue.equals(subledgerValue);
    }

    /**
     * Compare numeric values
     */
    private int compareNumeric(Object value1, Object value2) {
        if (value1 instanceof BigDecimal && value2 instanceof BigDecimal) {
            return ((BigDecimal) value1).compareTo((BigDecimal) value2);
        }
        if (value1 instanceof Number && value2 instanceof Number) {
            double d1 = ((Number) value1).doubleValue();
            double d2 = ((Number) value2).doubleValue();
            return Double.compare(d1, d2);
        }
        return 0;
    }

    /**
     * Get field value using reflection
     */
    private Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            // Try with different casing
            try {
                for (Field field : object.getClass().getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase(fieldName)) {
                        field.setAccessible(true);
                        return field.get(object);
                    }
                }
            } catch (Exception ex) {
                // Return null if field not found
            }
            return null;
        }
    }
}
