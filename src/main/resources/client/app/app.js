'use strict';

/**
 * MyTube application.
 */
angular.module('mytube',
        // Dependencies
        ['ui.router', 'restangular', 'firebase'])

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
  var clientId = 'GOOGLE_CLIENT_ID';
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