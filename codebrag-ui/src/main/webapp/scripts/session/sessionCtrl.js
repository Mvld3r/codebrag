angular.module('codebrag.session')

    .controller('SessionCtrl', function SessionCtrl($scope, $rootScope, authService, $state, events, $window, $location) {

        $scope.user = {
            login: '',
            password: '',
            rememberme: false
        };

        $scope.login = function () {
            if (loginFormValid()) {
                logInUser();
            }
        };

        $scope.githubLogin = function () {
            var githubLoginUrl = '/rest/github/authenticate';
            $window.location.href = githubLoginUrl + '?redirectTo=' + $location.url();
        };

        $scope.isLogged = function () {
            return authService.isAuthenticated();
        };

        $scope.isNotLogged = function () {
            return authService.isNotAuthenticated();
        };

        $scope.loggedInUser = function () {
            if (!authService.isAuthenticated()) {
                throw new Error("Cannot access current user, not authenticated");
            }
            return authService.loggedInUser;
        };

        $scope.logout = function () {
            authService.logout().then(function () {
                $state.transitionTo('home');
            });
        };

        function clearPasswordField() {
            $scope.loginForm.password.$dirty = false;
            $scope.user.password = '';
        }

        function clearLoginField() {
            $scope.loginForm.login.$dirty = false;
            $scope.user.login = '';
        }

        function loginFormValid() {
            // set dirty to show error messages on empty fields when submit is clicked
            $scope.loginForm.login.$dirty = true;
            $scope.loginForm.password.$dirty = true;
            return $scope.loginForm.$invalid === false
        }

        function logInUser() {
            authService.login($scope.user).then(function() {
                clearLoginField();
                clearPasswordField();
            }, function (errorResponse) {
                clearPasswordField();
                if (errorResponse.status === 401) {
                    $rootScope.$broadcast(events.httpAuthError, {status: 401, text: 'Invalid credentials'})
                }
            });
        }

    });