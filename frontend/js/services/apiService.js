/**
 * API Service for backend communication
 */
app.factory('ApiService', ['$http', 'API_CONFIG', function($http, API_CONFIG) {
    var baseUrl = API_CONFIG.baseUrl;

    return {
        // ========== Rule Group APIs ==========
        getRuleGroups: function() {
            return $http.get(baseUrl + '/rules/groups');
        },

        getActiveRuleGroups: function() {
            return $http.get(baseUrl + '/rules/groups/active');
        },

        getRuleGroupById: function(ruleGroupId) {
            return $http.get(baseUrl + '/rules/groups/' + ruleGroupId);
        },

        createRuleGroup: function(ruleGroup) {
            return $http.post(baseUrl + '/rules/groups', ruleGroup);
        },

        updateRuleGroup: function(ruleGroupId, ruleGroup) {
            return $http.put(baseUrl + '/rules/groups/' + ruleGroupId, ruleGroup);
        },

        // ========== Rule APIs ==========
        getAllRules: function() {
            return $http.get(baseUrl + '/rules');
        },

        getRulesByGroupId: function(ruleGroupId) {
            return $http.get(baseUrl + '/rules/groups/' + ruleGroupId + '/rules');
        },

        getRuleById: function(ruleId) {
            return $http.get(baseUrl + '/rules/' + ruleId);
        },

        createRule: function(rule) {
            return $http.post(baseUrl + '/rules', rule);
        },

        updateRule: function(ruleId, rule) {
            return $http.put(baseUrl + '/rules/' + ruleId, rule);
        },

        deleteRule: function(ruleId) {
            return $http.delete(baseUrl + '/rules/' + ruleId);
        },

        // ========== Matching Criteria APIs ==========
        getCriteriaByRuleId: function(ruleId) {
            return $http.get(baseUrl + '/rules/' + ruleId + '/criteria');
        },

        createCriteria: function(ruleId, criteria) {
            return $http.post(baseUrl + '/rules/' + ruleId + '/criteria', criteria);
        },

        deleteCriteria: function(criteriaId) {
            return $http.delete(baseUrl + '/rules/criteria/' + criteriaId);
        },

        // ========== Reconciliation APIs ==========
        executeReconciliation: function(reconData) {
            return $http.post(baseUrl + '/reconciliation/execute', reconData);
        },

        getAllRuns: function() {
            return $http.get(baseUrl + '/reconciliation/runs');
        },

        getRunById: function(runId) {
            return $http.get(baseUrl + '/reconciliation/runs/' + runId);
        },

        getMatchesForRun: function(runId) {
            return $http.get(baseUrl + '/reconciliation/runs/' + runId + '/matches');
        },

        getExceptionsForRun: function(runId) {
            return $http.get(baseUrl + '/reconciliation/runs/' + runId + '/exceptions');
        },

        getOpenExceptions: function() {
            return $http.get(baseUrl + '/reconciliation/exceptions/open');
        },

        updateExceptionStatus: function(exceptionId, data) {
            return $http.put(baseUrl + '/reconciliation/exceptions/' + exceptionId, data);
        },

        getStatistics: function() {
            return $http.get(baseUrl + '/reconciliation/statistics');
        }
    };
}]);
