(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
      .controller('ApproveCtrl', ApproveCtrl);

  /** @ngInject */
  function ApproveCtrl($scope, $http) {
    var getCertsApi = "http://localhost:3030/certs";
    $scope.approveList = [];
    $scope.filterData = {
      "startDate": '',
      "endDate": '',
      "companyName": ''
    };

    //获取证书列表数据
    $http.get(getCertsApi)
      .success(function(data){
        $scope.approveList = data;
      });

    //查询
    $scope.filter = function(){
      
    }
  }

})();

