(function () {
    'use strict';

    angular.module('BlurAdmin.pages.submit', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('submit', {
                url: '/submit',
                templateUrl: 'app/pages/submit/submit.html',
                title: 'submit',
                sidebarMeta: {
                    order: 800,
                },
            });
    }

})();