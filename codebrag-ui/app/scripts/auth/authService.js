angular.module('codebrag.auth').factory('authService', function ($http, httpRequestsBuffer, $q, $rootScope, events, User) {
    'use strict';

    var sessionApiUrl = 'rest/session',
        registrationApiUrl = 'rest/register/first-registration',
        currentUser = User.guest(),
        authenticatedDeferred = $q.defer(),
        currentUserRequest;

    function setLoggedInUser(userData) {
        currentUserRequest = null;
        currentUser.loggedInAs(userData);
        updateLoggedInUserInRootScope(currentUser, $rootScope);
        $rootScope.$broadcast(events.loggedIn);
        authenticatedDeferred.resolve(currentUser);
    }

    function updateLoggedInUserInRootScope(loggedInUser, $rootScope) {
        $rootScope.loggedInUser = loggedInUser;
    }


    return {

        login: function (user) {
            var loginRequest = $http.post(sessionApiUrl, user, {bypassInterceptors: true});
            return loginRequest.then(function (response) {
                setLoggedInUser(response.data);
                httpRequestsBuffer.retryAllRequest();
                return currentUser;
            }, function(response) {
                return $q.reject(response.data);
            });
        },

        logout: function () {
            return $http.delete(sessionApiUrl);
        },

        requestCurrentUser: function () {
            if (currentUser.isAuthenticated()) {
                return $q.when(currentUser);
            } else if (currentUserRequest) {
                return currentUserRequest;
            } else {
                currentUserRequest = $http.get(sessionApiUrl).then(function (response) {
                    setLoggedInUser(response.data);
                    return $q.when(currentUser);
                });
                return currentUserRequest;
            }

        },

        isFirstRegistration: function () {
            if (currentUser.isAuthenticated()) {
                return $q.reject();
            }
            return $http.get(registrationApiUrl).then(function (response) {
                var firstRegistration = response.data.firstRegistration;
                return $q.when(firstRegistration);
            });
        },

        loggedInUser: currentUser,

        userAuthenticated: authenticatedDeferred.promise

    };

});