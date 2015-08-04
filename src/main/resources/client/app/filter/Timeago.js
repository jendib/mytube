'use strict';

/**
 * Filter formatting an elapsed time.
 */
angular.module('mytube').filter('timeago', function() {
  return function(t) {
    if (!t) {
      return '';
    }
    return Math.round((new Date().getTime() - t) / 60000) + 'min';
  }
})