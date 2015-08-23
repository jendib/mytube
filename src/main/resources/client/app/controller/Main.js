'use strict';

/**
 * Main controller.
 */
angular.module('mytube').controller('Main', function($scope, Restangular, Firebase, $rootScope) {
  $scope.watchLaterPlaylist = Firebase.getPlaylist('WL');
  $scope.viewedVideos = Firebase.getViewedVideos();
  $scope.error = false;
  $scope.firebaseReady = false;

  $scope.viewedVideos.$loaded(function() {
    $scope.firebaseReady = true;
  });

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

  var syncViewedVideos = function() {
    if (!$scope.data) {
      return;
    }

    $scope.viewedVideos.$loaded(function() {
      console.log('syncing viewed videos');

      var viewedVideosId = _.pluck($scope.viewedVideos, '$value');

      _.each($scope.data.videos, function(video) {
        video.viewed = viewedVideosId.indexOf(video.id) != -1;

        if (!video.viewed) {
          console.log('adding video ' + video.id + ' to viewed videos');
          $scope.viewedVideos.$add(video.id);
        }
      });


      var videosId = _.pluck($scope.data.videos, 'id');
      for (var i = 0; i < $scope.viewedVideos.length; i++) {
        if (videosId.indexOf($scope.viewedVideos[i].$value) == -1) {
          console.log('video ' + $scope.viewedVideos[i].$value + ' (' + i + ') not in actual data, cleaning up');
          $scope.viewedVideos.$remove(i);
        }
      }
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

      syncViewedVideos();
      syncWatchLater();
    }, function () {
      $scope.error = true;
    });
  };
  $scope.reload();
});