'use strict';

/**
 * Watch later controller.
 */
angular.module('mytube').controller('WatchLater', function($scope, Firebase) {
  $scope.data = Firebase.getPlaylist('WL');

  $scope.watchlater = function(video) {
    $scope.data.$remove(video);
  };

  $scope.addFromUrl = function() {
    if (!$scope.videoId) {
      return;
    }

    var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
    var videoId = null;
    var match = $scope.videoId.match(regExp);
    if (match && match[7].length == 11){
      videoId = match[7];
    } else {
      alert('Not a valid YouTube URL');
      return;
    }

    gapi.client.youtube.videos.list({
      id: videoId,
      part: 'snippet,contentDetails,statistics'
    }).execute(function(response) {
          var video = response.items[0];
          $scope.videoId = '';

          $scope.data.$add({
            id: video.id,
            title: video.snippet.title,
            description: video.snippet.description,
            channel_id: video.snippet.channelId,
            channel_title: video.snippet.channelTitle,
            published_date: Date.parse(video.snippet.publishedAt),
            view_count: video.statistics.viewCount,
            like_count: video.statistics.likeCount,
            dislike_count: video.statistics.dislikeCount,
            duration: video.contentDetails.duration,
            thumbnails: {
              medium: {
                url: video.snippet.thumbnails.medium.url,
                width: video.snippet.thumbnails.medium.width,
                height: video.snippet.thumbnails.medium.height
              },
              high: {
                url: video.snippet.thumbnails.high.url,
                width: video.snippet.thumbnails.high.width,
                height: video.snippet.thumbnails.high.height
              }
            }
          });
        });
  };
});