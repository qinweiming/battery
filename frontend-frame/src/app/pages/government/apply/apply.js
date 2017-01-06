(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
      .controller('ApplyCtrl', ApplyCtrl);

  /** @ngInject */
  function ApplyCtrl($scope, $http, toastr) {
    var applyApi = 'http://localhost:3030/certs';

      $scope.fileStatus="";
      $scope.fileUpload = {
          url: 'http://0.0.0.0:8000',
          options: {
              multi_selection: false
              },
          callbacks: {
              filesAdded: function(uploader, files) {
                  $scope.loading = true;
                  uploader.start();
              },
              uploadProgress: function(uploader, file) {
                  $scope.loading = file.percent/100.0;
              },
              fileUploaded: function(uploader, file, response) {
                  $scope.loading = false;
                  $scope.fileStatus="上传成功";
              },
              error: function(uploader, error) {
                  $scope.loading = false;
                  $scope.fileStatus=error.message;
              }
          }
      };

    //默认值
    $scope.applyData = {
      'companyType': '',
      'companyName': '',
      'creditCode': '',
      'factoryCode': '',
      'contact': '',
      'phone': '',
      'email': '',
      'certFile': ''
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

