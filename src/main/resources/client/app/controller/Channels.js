'use strict';

/**
 * Channels controller.
 */
angular.module('mytube').controller('Channels', function($scope, Restangular, Firebase) {
  $scope.channels = Firebase.getChannels();
  $scope.tags = Firebase.getTags();

  Restangular.one('channels').get().then(function (data) {
    $scope.error = false;
    $scope.data = data;

    $scope.channels.$loaded(function() {
      $scope.tags.$loaded(function() {
        _.each($scope.data.channels, function(channel) {
          var channels =_.where($scope.channels, { id: channel.id });
          if (_.size(channels) > 0) {
            channel.tags = channels[0].tags;
          }
        });
      });
    });
  }, function () {
    $scope.error = true;
  });

  $scope.addTag = function() {
    $scope.tags.$add({
      id: guid(),
      name: $scope.tagName,
      active: true
    });
    $scope.tagName = '';
  };

  $scope.deleteTag = function(tag) {
    $scope.tags.$remove(tag);
  };

  $scope.saveTag = function(tag) {
    $scope.tags.$save(tag);
  }

  $scope.saveTags = function(channel) {
    var channels =_.where($scope.channels, { id: channel.id });
    if (_.size(channels) > 0) {
      channels[0].tags = channel.tags;
      $scope.channels.$save(channels[0]);
    } else {
      $scope.channels.$add({
        id: channel.id,
        tags: channel.tags
      });
    }
  };
});