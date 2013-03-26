"use strict";

angular.module("ajaxthrobber", []);

angular.module('codebrag.common.services', []);
angular.module('codebrag.common.filters', []);

angular.module('codebrag.session', ['ngCookies']);
angular.module('codebrag.commits', ['ngResource']);


angular.module('codebrag', [
            'codebrag.session',
            'codebrag.common.filters',
            'codebrag.common.services',
            'codebrag.commits',
            'ajaxthrobber'])
    .config(function ($routeProvider) {
        $routeProvider.
            when("/error404", {controller: 'SessionCtrl', templateUrl: "views/errorpages/error404.html"}).
            when("/error500", {controller: 'SessionCtrl', templateUrl: "views/errorpages/error500.html"}).
            when("/error", {controller: 'SessionCtrl', templateUrl: "views/errorpages/error500.html"}).
            otherwise({redirectTo: '/error404'});
    })

    .run(function ($rootScope, $location, authService, flashService) {
        $rootScope.$on("$routeChangeStart", function (event, next, current) {
            var nextRouteIsSecured = (typeof next.templateUrl !== "undefined") && next.templateUrl.indexOf("/secured/") > -1;
            if (authService.isNotAuthenticated() && nextRouteIsSecured) {
                $location.search("page", $location.url()).path("/login");
            }
        });
        $rootScope.$on("$routeChangeSuccess", function () {
            var message = flashService.get();
            if (angular.isDefined(message)) {
                showInfoMessage(message);
            }
        });
        authService.requestCurrentUser();
    });