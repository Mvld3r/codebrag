angular.module('codebrag.branches')

    .factory('currentRepoContext', function(authService, $rootScope, $q, events) {

        var currentContext,
            contextReady = $q.defer(),
            TO_REVIEW = 'pending',
            ALL = 'all';

        currentContext = {
            commitsFilter: TO_REVIEW,
            repo: null,
            branch: null,

            ready: function () {
                return contextReady.promise;
            },

            isToReviewFilterSet: function () {
                return this.commitsFilter === TO_REVIEW;
            },

            switchBranch: function (newBranch) {
                this.branch = newBranch;
                $rootScope.$broadcast(events.branches.branchChanged, newBranch);
            },

            switchCommitsFilter: function (newFilter) {
                this.commitsFilter = (newFilter === TO_REVIEW ? newFilter : ALL);
                $rootScope.$broadcast(events.commitsListFilterChanged, this.commitsFilter);
            }
        };

        authService.userAuthenticated.then(function(user) {
            currentContext.branch = user.settings.selectedBranch;
            currentContext.repo  = 'codebrag';  // TODO: fetch from authenticated user
            contextReady.resolve();
        });

        return currentContext;

    });