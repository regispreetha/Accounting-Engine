/**
 * Main AngularJS Application Module
 * Reconciliation Engine
 */
var app = angular.module('reconciliationApp', ['ngRoute']);

// API Configuration
app.constant('API_CONFIG', {
    baseUrl: 'http://localhost:8080/api'
});

// Route Configuration
app.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider
        .when('/dashboard', {
            templateUrl: 'views/dashboard.html',
            controller: 'DashboardController'
        })
        .when('/rules', {
            templateUrl: 'views/rules.html',
            controller: 'RulesController'
        })
        .when('/reconciliation', {
            templateUrl: 'views/reconciliation.html',
            controller: 'ReconciliationController'
        })
        .when('/runs', {
            templateUrl: 'views/runs.html',
            controller: 'RunsController'
        })
        .when('/runs/:runId', {
            templateUrl: 'views/run-details.html',
            controller: 'RunDetailsController'
        })
        .when('/exceptions', {
            templateUrl: 'views/exceptions.html',
            controller: 'ExceptionsController'
        })
        .otherwise({
            redirectTo: '/dashboard'
        });
}]);

// Error Handler Service
app.factory('ErrorHandler', function() {
    return {
        handle: function(error) {
            console.error('Error:', error);
            var message = 'An error occurred';
            if (error.data && error.data.message) {
                message = error.data.message;
            } else if (error.message) {
                message = error.message;
            }
            alert(message);
        }
    };
});

// Loading Indicator Service
app.factory('LoadingIndicator', function() {
    var loadingCount = 0;
    return {
        start: function() {
            loadingCount++;
        },
        stop: function() {
            loadingCount--;
        },
        isLoading: function() {
            return loadingCount > 0;
        }
    };
});
