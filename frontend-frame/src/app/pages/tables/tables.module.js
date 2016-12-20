/**
 * @author v.lugovsky
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.tables', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('tables', {
          url: '/tables',
          template : '<ui-view></ui-view>',
          abstract: true,
          controller: 'TablesPageCtrl',
          title: '政府系统（内部）',
          sidebarMeta: {
            icon: 'ion-grid',
            order: 300,
          },
        }).state('tables.basic', {
          url: '/basic',
          templateUrl: 'app/pages/tables/basic/tables.html',
          title: 'Basic Tables',
          sidebarMeta: {
            order: 0,
          },
        }).state('tables.smart', {
          url: '/smart',
          templateUrl: 'app/pages/tables/smart/tables.html',
          title: 'Smart Tables',
          sidebarMeta: {
            order: 100,
          },
        }).state('tables.hello', {
          url: '/hello',
          templateUrl: 'app/pages/tables/hello/tables.html',
          title: 'Hello Tables',
          sidebarMeta: {
            order: 100,
          },
        }).state('tables.ww', {
          url: '/ww',
          templateUrl: 'app/pages/tables/ww/tables.html',
          title: '审批与发放证书',
          sidebarMeta: {
            order: 100,
          },
        }).state('tables.shenqin', {
          url: '/shenqin',
          templateUrl: 'app/pages/form/inputs - 副本/inputs.html',
          title: '申请证书',
          sidebarMeta: {
            order: 101,
          },
        })
        ;
    $urlRouterProvider.when('/tables','/tables/basic');
  }

})();
