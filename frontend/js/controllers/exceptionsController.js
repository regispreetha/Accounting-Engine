/**
 * Exceptions Controller
 */
app.controller('ExceptionsController', ['$scope', 'ApiService', 'ErrorHandler',
    function($scope, ApiService, ErrorHandler) {

        $scope.exceptions = [];
        $scope.filteredExceptions = [];
        $scope.selectedFilter = 'OPEN';
        $scope.selectedException = null;
        $scope.resolutionData = {
            status: '',
            resolutionComments: '',
            updatedBy: 'SYSTEM'
        };

        // Load exceptions
        $scope.loadExceptions = function() {
            ApiService.getOpenExceptions().then(function(response) {
                $scope.exceptions = response.data;
                $scope.filterExceptions();
            }, ErrorHandler.handle);
        };

        // Filter exceptions by status
        $scope.filterExceptions = function() {
            if ($scope.selectedFilter === 'ALL') {
                $scope.filteredExceptions = $scope.exceptions;
            } else {
                $scope.filteredExceptions = $scope.exceptions.filter(function(exception) {
                    return exception.status === $scope.selectedFilter;
                });
            }
        };

        // Get severity badge class
        $scope.getSeverityClass = function(severity) {
            switch(severity) {
                case 'CRITICAL': return 'badge bg-danger';
                case 'HIGH': return 'badge bg-warning';
                case 'MEDIUM': return 'badge bg-info';
                case 'LOW': return 'badge bg-secondary';
                default: return 'badge bg-secondary';
            }
        };

        // Get status badge class
        $scope.getStatusClass = function(status) {
            switch(status) {
                case 'OPEN': return 'badge bg-danger';
                case 'IN_PROGRESS': return 'badge bg-warning';
                case 'RESOLVED': return 'badge bg-success';
                case 'CLOSED': return 'badge bg-secondary';
                default: return 'badge bg-secondary';
            }
        };

        // Select exception for resolution
        $scope.selectException = function(exception) {
            $scope.selectedException = exception;
            $scope.resolutionData.status = exception.status;
        };

        // Update exception status
        $scope.updateException = function() {
            if (!$scope.selectedException) {
                alert('Please select an exception');
                return;
            }

            ApiService.updateExceptionStatus(
                $scope.selectedException.exceptionId,
                $scope.resolutionData
            ).then(function(response) {
                alert('Exception updated successfully!');
                $scope.selectedException = null;
                $scope.resolutionData = {
                    status: '',
                    resolutionComments: '',
                    updatedBy: 'SYSTEM'
                };
                $scope.loadExceptions();
            }, ErrorHandler.handle);
        };

        // Cancel resolution
        $scope.cancelResolution = function() {
            $scope.selectedException = null;
            $scope.resolutionData = {
                status: '',
                resolutionComments: '',
                updatedBy: 'SYSTEM'
            };
        };

        // Initialize
        $scope.loadExceptions();
    }
]);
