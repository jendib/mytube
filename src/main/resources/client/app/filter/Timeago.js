'use strict';

/**
 * Filter formatting an elapsed time.
 */
angular.module('mytube').filter('timeago', function() {
  return function(t) {
    if (!t) {
      return '';
    }

    var minAgo = (new Date().getTime() - t) / 60000;

    if (minAgo < 60) {
      return Math.round(minAgo) + 'min';
    }

    var hourAgo = minAgo / 60;
    if (hourAgo < 24) {
      var t = Math.round(hourAgo);
      return t + ' hour' + (t > 1 ? 's' : '');
    }

    var dayAgo = hourAgo / 24;
    var t = Math.round(dayAgo);
    return t + ' day' + (t > 1 ? 's' : '');
  }
})