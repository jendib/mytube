'use strict';

/**
 * Main controller.
 */
angular.module('mytube').controller('Main', function($scope, Restangular, Firebase, $rootScope) {
  $scope.watchLaterPlaylist = Firebase.getPlaylist('WL');
  $scope.error = false;

  $scope.watchlater = function(video) {
    var inPlaylist = _.where($scope.watchLaterPlaylist, { id: video.id });
    if (_.size(inPlaylist) == 0) {
      $scope.watchLaterPlaylist.$add(video);
    } else {
      _.each(inPlaylist, function(video) {
        $scope.watchLaterPlaylist.$remove(video);
      });
    }
  };

  var syncWatchLater = function() {
    if (!$scope.data) {
      return;
    }

    var watchLaterIds = _.pluck($scope.watchLaterPlaylist, 'id');
    _.each($scope.data.videos, function(video) {
      video.watchinglater = watchLaterIds.indexOf(video.id) != -1;
    });
  };

  $scope.watchLaterPlaylist.$watch(function() {
    $scope.$apply(function() {
      syncWatchLater();
    });
  });

  $scope.reload = function() {
    $scope.data = null;
    $scope.error = false;
    Restangular.one('data').get().then(function (data) {
      $scope.error = false;
      $scope.data = data;
      $rootScope.time = data.time;
      syncWatchLater();
    }, function () {
      $scope.error = true;
    });
  };
  $scope.reload();
});