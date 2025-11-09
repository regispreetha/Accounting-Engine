/**
 * Reconciliation Execution Controller
 */
app.controller('ReconciliationController', ['$scope', '$location', 'ApiService', 'ErrorHandler',
    function($scope, $location, ApiService, ErrorHandler) {

        $scope.ruleGroups = [];
        $scope.reconciliationData = {
            runName: '',
            ruleGroupId: null,
            periodStartDate: '',
            periodEndDate: '',
            createdBy: 'SYSTEM'
        };

        $scope.isExecuting = false;
        $scope.executionResult = null;

        // Load active rule groups
        $scope.loadRuleGroups = function() {
            ApiService.getActiveRuleGroups().then(function(response) {
                $scope.ruleGroups = response.data;
            }, ErrorHandler.handle);
        };

        // Execute reconciliation
        $scope.executeReconciliation = function() {
            if (!$scope.reconciliationData.runName) {
                alert('Please enter a run name');
                return;
            }

            if (!$scope.reconciliationData.ruleGroupId) {
                alert('Please select a rule group');
                return;
            }

            if (!$scope.reconciliationData.periodStartDate || !$scope.reconciliationData.periodEndDate) {
                alert('Please select start and end dates');
                return;
            }

            $scope.isExecuting = true;
            $scope.executionResult = null;

            ApiService.executeReconciliation($scope.reconciliationData)
                .then(function(response) {
                    $scope.isExecuting = false;
                    $scope.executionResult = response.data;
                    alert('Reconciliation executed successfully!');

                    // Optionally redirect to run details
                    if (confirm('Do you want to view the results?')) {
                        $location.path('/runs/' + response.data.runId);
                    }
                }, function(error) {
                    $scope.isExecuting = false;
                    ErrorHandler.handle(error);
                });
        };

        // Reset form
        $scope.resetForm = function() {
            $scope.reconciliationData = {
                runName: '',
                ruleGroupId: null,
                periodStartDate: '',
                periodEndDate: '',
                createdBy: 'SYSTEM'
            };
            $scope.executionResult = null;
        };

        // Initialize
        $scope.loadRuleGroups();
    }
]);
