'use strict';

/**
 * @ngdoc function
 * @name clientApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the clientApp
 */
angular.module('clientApp')
  .controller('MainCtrl', function ($scope,RestaurantService) {

    var opts = {
      lines: 16, // The number of lines to draw
      length: 8, // The length of each line
      width: 4, // The line thickness
      radius: 6, // The radius of the inner circle
      corners: 1, // Corner roundness (0..1)
      rotate: 0, // The rotation offset
      direction: 1, // 1: clockwise, -1: counterclockwise
      color: '#000', // #rgb or #rrggbb or array of colors
      speed: 1, // Rounds per second
      trail: 60, // Afterglow percentage
      shadow: false, // Whether to render a shadow
      hwaccel: false, // Whether to use hardware acceleration
      className: 'spinner-draw', // The CSS class to assign to the spinner
      zIndex: 2e9, // The z-index (defaults to 2000000000)
      top: 24, // Top position relative to parent
      left: 6 // Left position relative to parent
    };

    var target = document.getElementById('spooscoreSpinner');
    var spinner = new Spinner(opts).spin(target);

    console.log('MainCtrl is alive!');

    function getLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(setPosition);
      } else {
        alert('Geolocation is not supported by this browser.');
      }
    }

    $scope.geometry = {
      latitude: 0.0,
      longitude: 0.0
    };

    $scope.zipFieldNeeded = function(){
      return $scope.geometry.latitude === 0.0 && $scope.geometry.longitude === 0.0;
    };

    function setPosition(position) {
      $scope.$apply(function() {
        $scope.geometry = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        $scope.zipFieldNeeded();
        $scope.cityStateZip = '';
        console.log('geometry '+JSON.stringify($scope.geometry));
      });
    }

    $scope.detectGeometry = function (){
      getLocation();
      $scope.isCollapsed = false;
    };

    var createSearch = function(){
      var search = {};
      if ($scope.cityStateZip){
        if ($scope.cityStateZip.indexOf(',') > -1){
          var cityState = $scope.cityStateZip.split(',');
          search.city = _.trim(cityState[0]);
          search.state = _.trim(cityState[1]);
        } else {
          search.zipCode = $scope.cityStateZip;
        }
        $scope.geometry = {
          latitude: 0.0,
          longitude: 0.0
        };
      } else {
        search.latitude = $scope.geometry.latitude;
        search.longitude = $scope.geometry.longitude;
      }

      return search;
    };

    $scope.isCollapsed = true;

    $scope.loading = false;

   $scope.restaurants = RestaurantService.lastRestaurants;
    /*
    $scope.delay = 0;
    $scope.minDuration = 0;
    $scope.message = 'Please Wait...';
    $scope.backdrop = true;
    $scope.searchPromise = null;
    */
    $scope.search = function () {

      $scope.loading = true;
        console.log('Called search with ' + JSON.stringify($scope.cityStateZip));
      var restaurantSearch = createSearch();
      RestaurantService.findRestaurants(restaurantSearch)
        .then(function (response) {
          console.log('Search returned.');
          $scope.restaurants = response.data;
          $scope.loading = false;
        }, function (response) {
            $scope.restaurants = RestaurantService.lastRestaurants;
          alert('RestaurantSearch ERROR' + response.data);
        });
    };
    $scope.next = function () {
      $scope.loading = true;
      RestaurantService.nextRestaurants()
        .then(function (response) {
          console.log('Next returned.');
          $scope.restaurants = response.data;
          $scope.loading = false;
        }, function (response) {
          $scope.loading = false;
          alert('RestaurantNext ERROR' + response.data);
        });
    };

    var updateRestaurants = function(restaurantUpdate){
      var index = _.findIndex($scope.restaurants, function(restaurant) {
        return restaurant.restaurantID === restaurantUpdate.restaurantID;
      });
      console.log('Found '+ JSON.stringify(restaurantUpdate)+' at '+index);
      $scope.restaurants[index] = restaurantUpdate;
    };

    $scope.exceededTries = function(restaurant){
      if (restaurant.inspectionReportTries){
        return restaurant.inspectionReportTries >= 2;
      } else {
        return false;
      }
    };

    $scope.getInspectionReport = function(restaurant){

      $scope.loading = true;
      if (restaurant.inspectionReportTries){
        restaurant.inspectionReportTries = restaurant.inspectionReportTries + 1;
      } else {
        restaurant.inspectionReportTries = 1;
      }
      RestaurantService.getInspectionReport(restaurant)
        .then(function (response) {
        console.log('getInspectionReport returned.');
          $scope.loading = false;
          updateRestaurants(response.data);
      }, function (response) {
          $scope.loading = false;
        alert('getInspectionReport ERROR' + response.data);
      });

    };

    $scope.hasRestaurants = function(){
      if ($scope.restaurants){
        return $scope.restaurants.length > 0 ? true : false;
      } else {
        return false;
      }
    };



  })
  .directive('drawStarsJs', function () {
    return {
      restrict: 'A',
      scope: {
        options: '='
      },
      compile: function (tElem, tAttrs) {

        var drawStars = function (element, pOptions) {
          var options = pOptions || {};
          if (options) {
            var average = options.average;
            var radius = options.radius;
            //var holdStage = new createjs.Stage('starCanvas' + id);
            var stage = new createjs.Stage(element[0]);
            average = average || 0;
            var wholeInt = Math.floor(average);
            var diff = average - wholeInt;
            //console.log('diff ' + diff + ' average ' + average);
            for (var i = 0; i < wholeInt; i++) {
              var polystar2 = new createjs.Shape();
              polystar2.graphics.setStrokeStyle(1).beginStroke('#000000').beginFill('#FBB62B').drawPolyStar((i * radius * 2) + radius, radius, radius, 5, 0.5, -90);
              stage.addChild(polystar2);
            }
            if (diff > 0.0) {
              var polystar = new createjs.Shape();
              var fillPct = 4 * radius * (1 - diff);
              polystar.graphics.setStrokeStyle(1).beginStroke('#000000').beginLinearGradientFill(['#FFF', '#FBB62B'], [0.5, 0.5], 0, 0, 0, fillPct).drawPolyStar((wholeInt * radius * 2) + radius, radius, radius, 5, 0.5, -90);
              stage.addChild(polystar);
            }
            stage.update();
          }
        };

        if (tElem[0].tagName !== 'CANVAS') {
          throw new Error('drawStars can only be set on a canvas element. ' + tElem[0].tagName + ' will not work.');
        }

        return function (scope, element, attrs) {
          scope.$watch('options', function (newV, oldV) {
            drawStars(element, scope.options);
          }, true);

        };
      }
    };
  }).directive('markdown', function () {
    var converter = new Showdown.converter();
    return {
      restrict: 'AE',
      link: function (scope, element, attrs) {
        var htmlText = converter.makeHtml(element.text());
        element.html(htmlText);
      }
    };

  });
