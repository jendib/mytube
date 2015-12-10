'use strict';

/**
 * MyTube application.
 */
angular.module('mytube',
        // Dependencies
        ['ui.router', 'restangular', 'firebase', 'ui.bootstrap'])

    /**
     * Configuring modules.
     */
    .config(function ($stateProvider, RestangularProvider, $httpProvider, $uiViewScrollProvider) {
      // Configuring UI Router
      $uiViewScrollProvider.useAnchorScroll();
      $stateProvider
          .state('main', {
            url: '',
            views: {
              'page': {
                templateUrl: 'partial/main.html',
                controller: 'Main'
              }
            }
          })
          .state('watchlater', {
            url: '/watchlater',
            views: {
              'page': {
                templateUrl: 'partial/watchlater.html',
                controller: 'WatchLater'
              }
            }
          })
          .state('channels', {
            url: '/channels',
            views: {
              'page': {
                templateUrl: 'partial/channels.html',
                controller: 'Channels'
              }
            }
          });

      // Configuring Restangular
      RestangularProvider.setBaseUrl('.');
      RestangularProvider.setRequestSuffix('.json');

      // Configuring $http to act like jQuery.ajax
      $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
      $httpProvider.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
      $httpProvider.defaults.transformRequest = [function (data) {
        var param = function (obj) {
          var query = '';
          var name, value, fullSubName, subName, subValue, innerObj, i;

          for (name in obj) {
            value = obj[name];

            if (value instanceof Array) {
              for (i = 0; i < value.length; ++i) {
                subValue = value[i];
                fullSubName = name;
                innerObj = {};
                innerObj[fullSubName] = subValue;
                query += param(innerObj) + '&';
              }
            } else if (value instanceof Object) {
              for (subName in value) {
                subValue = value[subName];
                fullSubName = name + '[' + subName + ']';
                innerObj = {};
                innerObj[fullSubName] = subValue;
                query += param(innerObj) + '&';
              }
            }
            else if (value !== undefined && value !== null) {
              query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
            }
          }

          return query.length ? query.substr(0, query.length - 1) : query;
        };

        return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
      }];
    })

    /**
     * Application initialization.
     */
    .run(function ($rootScope, $state, $stateParams) {
      $rootScope.$state = $state;
      $rootScope.$stateParams = $stateParams;
    });

/**
 * Google API init.
 */
var googleApiInit = function() {
  var clientId = '665187980114-hqo98e9626jjoqjogev6onetok73a1c1.apps.googleusercontent.com';
  var scope = 'https://www.googleapis.com/auth/youtube';

  setTimeout(function() {
    gapi.auth.authorize({
      client_id: clientId,
      scope: scope,
      immediate: true
    }, function(auth) {
      if (auth.error == 'immediate_failed') {
        gapi.auth.authorize({
          client_id: clientId,
          scope: scope,
          immediate: false
        }, function(auth) {
          loadGoogleApi(auth.expires_in);
        });
      } else {
        loadGoogleApi(auth.expires_in);
      }
    });
  }, 1);
};

/**
 * Load Google API and refresh the token when expired.
 *
 * @param expire Expiration duration in seconds
 */
var loadGoogleApi = function(expire) {
  gapi.client.load('youtube', 'v3');
  setTimeout(function() {
    googleApiInit();
  }, expire * 1000)
};

var guid = function() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = crypto.getRandomValues(new Uint8Array(1))[0]%16|0, v = c == 'x' ? r : (r&0x3|0x8);
    return v.toString(16);
  });
};