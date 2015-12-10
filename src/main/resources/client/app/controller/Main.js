'use strict';

/**
 * Main controller.
 */
angular.module('mytube').controller('Main', function($scope, Restangular, Firebase, $rootScope) {
  $scope.watchLaterPlaylist = Firebase.getPlaylist('WL');
  $scope.viewedVideos = Firebase.getViewedVideos();
  $scope.tags = Firebase.getTags();
  $scope.channels = Firebase.getChannels();
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

  var syncTags = function() {
    $scope.channels.$loaded(function() {
      $scope.tags.$loaded(function() {
        _.each($scope.data.videos, function (video) {
          // For each video, check if its channel have active tags
          var channels = _.where($scope.channels, { id: video.channel_id });

          if (_.size(channels) == 0) {
            // The channel is not tagged, visible video by default
            video.visible = true;
          } else {
            var tags = channels[0].tags;
            // The channel is tagged, does it have an active tag?
            video.visible = false;
            var existingNotActiveTag = false;
            _.each(tags, function(active, id) {
              if (active) {
                var activeTags = _.where($scope.tags, { id: id });
                if (_.size(activeTags) > 0) {
                  // The tag exists
                  if (activeTags[0].active) {
                    video.visible = true;
                    if (video.tags) {
                      video.tags.push(activeTags[0]);
                    } else {
                      video.tags = [activeTags[0]];
                    }
                  } else {
                    // We have an exising tag but not active
                    existingNotActiveTag = true;
                  }
                }
              }
            });

            if (!existingNotActiveTag) {
              video.visible = true;9            }
          }
        });
      });
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
      syncTags();
    }, function () {
      $scope.error = true;
    });
  };
  $scope.reload();
});