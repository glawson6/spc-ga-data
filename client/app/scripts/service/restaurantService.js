'use strict';

/**
 * @ngdoc function
 * @name clientApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the clientApp
 */
angular.module('clientApp')
  .service('RestaurantService', function ($http) {

    var that = this;
    that.lastRestaurants = [];
    that.restaurantSearch = {};
    that.offset = 0;

    console.log('RestaurantService is alive!');

    this.findRestaurants = function(restaurantSearch) {
      that.restaurantSearch = restaurantSearch;
      console.log('Called RestaurantService.findRestaurants with '+JSON.stringify(restaurantSearch));
      return $http({url: '/api/restaurant/search', method: 'POST', data: restaurantSearch})
        .success(function(data) {
          that.lastRestaurants = data;
          that.offset = that.offset + that.lastRestaurants.length;
        });
    };

    this.nextRestaurants = function() {
      var restaurantSearch = that.restaurantSearch;
      restaurantSearch.offset = that.offset;
      console.log('Called RestaurantService.nextRestaurants with '+JSON.stringify(restaurantSearch));
      return $http({url: '/api/restaurant/search', method: 'POST', data: restaurantSearch})
        .success(function(data) {
          that.lastRestaurants = data;
          that.offset = that.offset + that.lastRestaurants.length;
        });
    };

    this.getInspectionReport = function(restaurant) {
      console.log('Called RestaurantService.getInspectionReport with '+JSON.stringify(restaurant));
      return $http({url: '/api/restaurant/inspectionUpdate', method: 'POST', data: restaurant});
    };

  });
