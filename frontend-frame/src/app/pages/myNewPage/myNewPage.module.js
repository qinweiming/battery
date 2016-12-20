/**
 * @author v.lugovsky
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.myNewPage', [])
      .config(routeConfig);

  /** @ngInject */
function routeConfig($stateProvider) {
    $stateProvider
   .state('myNewPage', {
          url: '/myNewPage',
          
          templateUrl: 'app/pages/myNewPage/for-gover-list.html',
          controller: 'newTablesPageCtrl',
          title: 'page for goverment',
          sidebarMeta: {
            order: 800,
          },
        });
  }

})();
 
