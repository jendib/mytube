'use strict';

/**
 * Filter formatting a duration.
 */
angular.module('mytube').filter('duration', function() {
  return function(s) {
    var matches = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?$/.exec(s);
    var hours = 0, minutes = 0, seconds = 0;
    if (matches[1]) hours = Number(matches[1]);
    if (matches[2]) minutes = Number(matches[2]);
    if (matches[3]) seconds = Number(matches[3]);
    if (hours   < 10) hours   = '0' + hours;
    if (minutes < 10) minutes = '0' + minutes;
    if (seconds < 10) seconds = '0' + seconds;

    return (hours == '00' ? '': hours + ':') + minutes + ':' + seconds;
  }
});