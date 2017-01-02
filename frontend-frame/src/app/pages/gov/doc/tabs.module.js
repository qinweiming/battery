/**
 * @author v.lugovsky
 * created on 21.12.2015
 */
(function () {
  'use strict';

  angular.module('BlurAdmin.pages.gov.doc', [])
      .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
        .state('gov.doc', {
          url: '/doc',
          templateUrl: 'app/pages/gov/doc/doc.html',
          title: '帮助',
          sidebarMeta: {
            order: 800,
          },
        });
  }

})();
