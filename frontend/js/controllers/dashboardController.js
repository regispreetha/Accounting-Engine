/**
 * Dashboard Controller
 */
app.controller('DashboardController', ['$scope', 'ApiService', 'ErrorHandler',
    function($scope, ApiService, ErrorHandler) {

        $scope.statistics = {
            totalRuns: 0,
            openExceptions: 0
        };

        $scope.recentRuns = [];
        $scope.openExceptions = [];

        // Load dashboard data
        $scope.loadDashboard = function() {
            // Load statistics
            ApiService.getStatistics().then(function(response) {
                $scope.statistics = response.data;
            }, ErrorHandler.handle);

            // Load recent runs
            ApiService.getAllRuns().then(function(response) {
                $scope.recentRuns = response.data.slice(0, 5); // Get latest 5 runs
            }, ErrorHandler.handle);

            // Load open exceptions
            ApiService.getOpenExceptions().then(function(response) {
                $scope.openExceptions = response.data.slice(0, 10); // Get latest 10 exceptions
            }, ErrorHandler.handle);
        };

        // Initialize dashboard
        $scope.loadDashboard();

        // Refresh dashboard
        $scope.refresh = function() {
            $scope.loadDashboard();
        };
    }
]);
