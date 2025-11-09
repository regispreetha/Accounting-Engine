/**
 * Reconciliation Runs Controller
 */
app.controller('RunsController', ['$scope', 'ApiService', 'ErrorHandler',
    function($scope, ApiService, ErrorHandler) {

        $scope.runs = [];
        $scope.filteredRuns = [];
        $scope.searchText = '';

        // Load all runs
        $scope.loadRuns = function() {
            ApiService.getAllRuns().then(function(response) {
                $scope.runs = response.data;
                $scope.filteredRuns = response.data;
            }, ErrorHandler.handle);
        };

        // Search/filter runs
        $scope.filterRuns = function() {
            if (!$scope.searchText) {
                $scope.filteredRuns = $scope.runs;
                return;
            }

            $scope.filteredRuns = $scope.runs.filter(function(run) {
                return run.runName.toLowerCase().includes($scope.searchText.toLowerCase()) ||
                       run.status.toLowerCase().includes($scope.searchText.toLowerCase());
            });
        };

        // Get status badge class
        $scope.getStatusClass = function(status) {
            switch(status) {
                case 'COMPLETED': return 'badge bg-success';
                case 'FAILED': return 'badge bg-danger';
                case 'IN_PROGRESS': return 'badge bg-warning';
                default: return 'badge bg-secondary';
            }
        };

        // Initialize
        $scope.loadRuns();
    }
]);

/**
 * Run Details Controller
 */
app.controller('RunDetailsController', ['$scope', '$routeParams', 'ApiService', 'ErrorHandler',
    function($scope, $routeParams, ApiService, ErrorHandler) {

        $scope.run = null;
        $scope.matches = [];
        $scope.exceptions = [];
        $scope.activeTab = 'summary';

        var runId = $routeParams.runId;

        // Load run details
        $scope.loadRunDetails = function() {
            ApiService.getRunById(runId).then(function(response) {
                $scope.run = response.data;
            }, ErrorHandler.handle);

            ApiService.getMatchesForRun(runId).then(function(response) {
                $scope.matches = response.data;
            }, ErrorHandler.handle);

            ApiService.getExceptionsForRun(runId).then(function(response) {
                $scope.exceptions = response.data;
            }, ErrorHandler.handle);
        };

        // Set active tab
        $scope.setActiveTab = function(tab) {
            $scope.activeTab = tab;
        };

        // Initialize
        $scope.loadRunDetails();
    }
]);
