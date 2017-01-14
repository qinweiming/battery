/**
 * @author zhaoxiaoyong
 * created on 2016.12.31
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('government', {
          url: '/government',
          template : '<ui-view></ui-view>',
          abstract: true,
          title: '认证中心',
          controller: 'governmentCtrl',
          sidebarMeta: {
            /*
            ** 一级菜单可以设置icon，二级菜单不可以
            ** icon素材来源：http://ionicons.com
            ** 找到合适的icon，填入其class名称
            */
            icon: 'ion-soup-can-outline',
            order: 100,
          },
        }).state('government.approve', {
          url: '/approve',
          templateUrl: 'app/pages/government/approve/approve.html',
          controller: 'ApproveCtrl',
          title: '审批与发放证书',
          sidebarMeta: {
            order: 152,
          },
        }).state('government.apply', {
          url: '/apply',
          templateUrl: 'app/pages/government/apply/apply.html',
          controller: 'ApplyCtrl',
          title: '申请证书',
          sidebarMeta: {
            order: 151,
          }
        }).state('government.view', {
          url: '/view',
          templateUrl: 'app/pages/government/view/view.html',
         // controller: 'ViewCtrl',
          title: '已审批证书查看',
          sidebarMeta: {
            order: 153,
          },
        }).state('government.trace', {
          url: '/trace',
          templateUrl: 'app/pages/government/trace/trace.html',
         //controller: 'TraceCtrl',
          title: '追溯查询',
          sidebarMeta: {
            order: 154,
          },
        }).state('government.distribution', {
          url: '/distribution',
          templateUrl: 'app/pages/government/distribution/distribution.html',
          controller: 'DistributionCtrl',
          title: '密度分布',
          sidebarMeta: {
            order: 156,
          },
        }).state('government.flow', {
          url: '/flow',
          templateUrl: 'app/pages/government/flow/flow.html',
          controller: 'FlowCtrl',
          title: '流向图',
          sidebarMeta: {
            order: 158,
          },
        });
    $urlRouterProvider.when('/government','/government/apply');
  }

})();
