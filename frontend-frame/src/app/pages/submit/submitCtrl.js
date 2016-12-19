(function () {
    'use strict';

    angular.module('BlurAdmin.pages.submit')
        .controller('submitCtrl', submitCtrl);

    /** @ngInject */
    function submitCtrl($scope,$http) {
        $scope.user = {name: "", credit: "", id: "", contact: "", tel: "", email: ""};
        /* $scope.processForm= function(){
                var pData = {name:$scope.user.name,
                             credit:$scope.user.credit,
                             id:$scope.user.id,
                             contact:$scope.user.contact,
                             tel:$scope.user.tel,
                             email:$scope.user.email
                };
                $http({method:'POST',url:'http://loaclhost:5000/user',params:pData}).
                    success(function(response) {
                        $scope.ansInfo=response.a;});*/
             $scope.select=
                 [
                     { label: '汽车厂', able: true },
                     { label: '电池厂', able: false }

                 ];

        }

})();
