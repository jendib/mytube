'use strict';

/**
 * Firebase service.
 */
angular.module('mytube').factory('Firebase', function($firebaseArray) {
  return {
    getPlaylist: function(playlistId) {
      var ref = new Firebase("https://FIREBASE_ID.firebaseio.com/mytube/playlist/" + playlistId);
      return $firebaseArray(ref);
    }
  }
});