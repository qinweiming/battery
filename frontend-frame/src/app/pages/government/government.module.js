/**
 * @author zhaoxiaoyong
 * created on 2016.12.31
 */
(function () {
    'use strict';

    angular.module('BlurAdmin.pages.government',[])
        .config(routeConfig)/*.config(pluploadConfig)*/;


    /** @ngInject */
    function routeConfig($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('government', {
                url: '/government',
                template: '<ui-view></ui-view>',
                abstract: true,
                title: '认证中心',
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
                    order: 100,
                },
            }).state('government.apply', {
                url: '/apply',
                templateUrl: 'app/pages/government/apply/apply.html',
                controller: 'ApplyCtrl',
                title: '申请证书',
                sidebarMeta: {
                    order: 101,
                }
            }).state('government.density', {
                url: '/density',
                templateUrl: 'app/pages/government/density/density.html',
                title: '密度分布图',
                sidebarMeta: {
                    order: 102,
                }
            }).state('government.alreadyApprove', {
                url: '/alreadyApprove',
                templateUrl: 'app/pages/government/alreadyApprove/alreadyApprove.html',
                title: '已审批证书查看',
                sidebarMeta: {
                    order: 103,
                }
            })
        ;
        $urlRouterProvider.when('/government', '/government/apply');
         }

   /* function pluploadConfig(pluploadOptionProvider){
        pluploadOptionProvider.setOptions({
            flash_swf_url: 'bower_components/plupload/js/Moxie.swf',
            silverlight_xap_url: 'bower_components/plupload/js/Moxie.xap'
        });
        }*/



})();
