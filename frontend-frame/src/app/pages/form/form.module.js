/**
 * @author v.lugovsky
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.form', ['ngResource'])
      .config(routeConfig);
  angular.module('BlurAdmin.pages.form').controller('MainCtrl', ['$scope','$http'],function ($scope,$http) {

    $http.get('http://localhost:8088/certs/1').then(function(response) {
        $scope.Cert = response.data;
      });

  });
    
  
   /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
        .state('form', {
          url: '/form',
          template : '<ui-view></ui-view>',
          abstract: true,
          title: 'Form Elements',
          sidebarMeta: {
            icon: 'ion-compose',
            order: 250,
          },
        })
        .state('form.inputs', {
          url: '/inputs',
          templateUrl: 'app/pages/form/inputs/inputs.html',
          title: 'Form Inputs',
          sidebarMeta: {
            order: 0,
          },
        })
        .state('form.inputs2', {
          url: '/inputs2',
          templateUrl: 'app/pages/form/inputs - 副本/inputs.html',
          // controller: 'MainCtrl',
          controller: function ($scope,$http) {
            $scope.companyName='abc company';
            $http.get('http://localhost:8088/certs/1').then(function(response) {
              
              $scope.Cert = response.data;
            });
          },
          title: '申请证书',
          sidebarMeta: {
            order: 1,
          },
        })
        .state('form.layouts', {
          url: '/layouts',
          templateUrl: 'app/pages/form/layouts/layouts.html',
          title: 'Form Layouts',
          sidebarMeta: {
            order: 100,
          },
        })
        .state('form.wizard',
        {
          url: '/wizard',
          templateUrl: 'app/pages/form/wizard/wizard.html',
          controller: 'WizardCtrl',
          controllerAs: 'vm',
          title: 'Form Wizard',
          sidebarMeta: {
            order: 200,
          },
        });
        
  }
})();
