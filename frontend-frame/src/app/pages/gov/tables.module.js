/**
 * @author v.lugovsky
 * created on 16.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.gov', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('gov', {
          url: '/gov',
          template : '<ui-view></ui-view>',
          abstract: true,
          controller: 'TablesPageCtrl',
          title: '政府系统（内部）',
          sidebarMeta: {
            icon: 'ion-grid',
            order: 300,
          },
        }).state('gov.basic', {
          url: '/basic',
          templateUrl: 'app/pages/gov/basic/tables.html',
          title: 'Basic Tables',
          sidebarMeta: {
            order: 0,
          },
        }).state('gov.smart', {
          url: '/smart',
          templateUrl: 'app/pages/gov/smart/tables.html',
          title: 'Smart Tables',
          sidebarMeta: {
            order: 100,
          },
        }).state('gov.trace', {
          url: '/trace',
          templateUrl: 'app/pages/gov/traces/trace.html',
          title: '追溯查询',
          sidebarMeta: {
            order: 104,
          },
        }) .state('gov.cert', {
          url: '/cert',
          templateUrl: 'app/pages/gov/cert/cert.html',
          title: '审批与发放证书',
          sidebarMeta: {
            order: 102,
          },
        }).state('gov.view', {
          url: '/view',
          templateUrl: 'app/pages/gov/view/view.html',
          title: '查看已审批证书',
          sidebarMeta: {
            order: 103,
          },
        }).state('gov.distr', {
          url: '/distr',
          templateUrl: 'app/pages/gov/distr/dist.html',
          title: '密度分布',
          sidebarMeta: {
            order: 105,
          },
        })
        .state('gov.flow', {
          url: '/flow',
          templateUrl: 'app/pages/gov/cert/cert.html',
          title: '流向图',
          sidebarMeta: {
            order: 106,
          },
        }).state('gov.doc', {
          url: '/doc',
          templateUrl: 'app/pages/gov/doc/doc.html',
          title: '帮助',
          sidebarMeta: {
            order: 107,
          },
        })
        .state('gov.apply', {
          url: '/apply',
          templateUrl: 'app/pages/gov/apply/apply.html',
          title: '申请证书',
          sidebarMeta: {
            order: 101,
          },
        })
        ;
    $urlRouterProvider.when('/gov','/gov/basic');
  }

})();
