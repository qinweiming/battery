(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
      .controller('ApplyCtrl', ApplyCtrl);

  /** @ngInject */
  function ApplyCtrl($scope, $http, toastr) {
    var applyApi = 'http://localhost:3030/certs';

    //默认值
    $scope.applyData = {
      /*'companyType': '1',
      'companyName': '某某电池厂',
      'creditCode': '',
      'factoryCode': '',
      'contact': '',
      'phone': '',
      'email': '',
      'certFile': ''*/
    };

    //确认录入
    $scope.confirm = function() {
      $http.post(applyApi, $scope.applyData)
        .success(function(data){
          toastr.success('录入成功', '', {
            "timeOut": "1000",
            "closeButton": false,
          });
        })
        .error(function(data){
          toastr.error('录入失败', '', {});
          console.log("error: ", data);
        });
    }
  }

})();

