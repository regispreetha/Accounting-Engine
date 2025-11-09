/**
 * Rules Builder Controller
 */
app.controller('RulesController', ['$scope', 'ApiService', 'ErrorHandler',
    function($scope, ApiService, ErrorHandler) {

        $scope.ruleGroups = [];
        $scope.selectedRuleGroup = null;
        $scope.rules = [];
        $scope.selectedRule = null;
        $scope.matchingCriteria = [];

        $scope.newRuleGroup = {};
        $scope.newRule = {};
        $scope.newCriteria = {};

        $scope.showRuleGroupForm = false;
        $scope.showRuleForm = false;
        $scope.showCriteriaForm = false;

        // Load rule groups
        $scope.loadRuleGroups = function() {
            ApiService.getRuleGroups().then(function(response) {
                $scope.ruleGroups = response.data;
            }, ErrorHandler.handle);
        };

        // Select rule group
        $scope.selectRuleGroup = function(ruleGroup) {
            $scope.selectedRuleGroup = ruleGroup;
            $scope.loadRulesForGroup(ruleGroup.ruleGroupId);
        };

        // Load rules for a group
        $scope.loadRulesForGroup = function(ruleGroupId) {
            ApiService.getRulesByGroupId(ruleGroupId).then(function(response) {
                $scope.rules = response.data;
            }, ErrorHandler.handle);
        };

        // Select rule
        $scope.selectRule = function(rule) {
            $scope.selectedRule = rule;
            $scope.loadCriteriaForRule(rule.ruleId);
        };

        // Load matching criteria for a rule
        $scope.loadCriteriaForRule = function(ruleId) {
            ApiService.getCriteriaByRuleId(ruleId).then(function(response) {
                $scope.matchingCriteria = response.data;
            }, ErrorHandler.handle);
        };

        // Create new rule group
        $scope.createRuleGroup = function() {
            ApiService.createRuleGroup($scope.newRuleGroup).then(function(response) {
                alert('Rule group created successfully!');
                $scope.newRuleGroup = {};
                $scope.showRuleGroupForm = false;
                $scope.loadRuleGroups();
            }, ErrorHandler.handle);
        };

        // Create new rule
        $scope.createRule = function() {
            if (!$scope.selectedRuleGroup) {
                alert('Please select a rule group first');
                return;
            }

            $scope.newRule.ruleGroupId = $scope.selectedRuleGroup.ruleGroupId;

            ApiService.createRule($scope.newRule).then(function(response) {
                alert('Rule created successfully!');
                $scope.newRule = {};
                $scope.showRuleForm = false;
                $scope.loadRulesForGroup($scope.selectedRuleGroup.ruleGroupId);
            }, ErrorHandler.handle);
        };

        // Create new matching criteria
        $scope.createCriteria = function() {
            if (!$scope.selectedRule) {
                alert('Please select a rule first');
                return;
            }

            ApiService.createCriteria($scope.selectedRule.ruleId, $scope.newCriteria)
                .then(function(response) {
                    alert('Matching criteria created successfully!');
                    $scope.newCriteria = {};
                    $scope.showCriteriaForm = false;
                    $scope.loadCriteriaForRule($scope.selectedRule.ruleId);
                }, ErrorHandler.handle);
        };

        // Delete rule
        $scope.deleteRule = function(ruleId) {
            if (confirm('Are you sure you want to delete this rule?')) {
                ApiService.deleteRule(ruleId).then(function(response) {
                    alert('Rule deleted successfully!');
                    $scope.loadRulesForGroup($scope.selectedRuleGroup.ruleGroupId);
                }, ErrorHandler.handle);
            }
        };

        // Delete criteria
        $scope.deleteCriteria = function(criteriaId) {
            if (confirm('Are you sure you want to delete this criteria?')) {
                ApiService.deleteCriteria(criteriaId).then(function(response) {
                    alert('Criteria deleted successfully!');
                    $scope.loadCriteriaForRule($scope.selectedRule.ruleId);
                }, ErrorHandler.handle);
            }
        };

        // Initialize
        $scope.loadRuleGroups();
    }
]);
