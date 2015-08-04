'use strict';

/**
 * Video directive.
 */
angular.module('mytube').directive('mytubeVideo', function() {
  return {
    restrict: 'E',
    templateUrl: 'partial/directive.video.html',
    replace: true,
    scope: {
      video: '=',
      watchlater: '&'
    }
  }
});