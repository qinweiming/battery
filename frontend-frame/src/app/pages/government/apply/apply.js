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



    //文件上传例子
    $scope.uploadFile = function(elem) {
        if (window.File && window.FileReader && window.FormData) {
            var file = elem.files[0];
            var notValidate=true;
            if (file) {
                console.log(file.type);

                if (/zip/i.test(file.type) || notValidate) {
                    readFile(file, sendFile);
                } else {
                    alert('Not a valid zip file!');
                }
            }
        } else {
            alert("File upload is not supported!");
        }
    }

    //readFile
    function readFile(file, callback) {
        var reader = new FileReader();

        reader.onloadend = function () {
            callback(reader.result);
        }

        reader.onerror = function () {
            alert('There was an error reading the file!');
        }

        reader.readAsDataURL(file);
    }

    //send file
    function sendFile(fileData) {
        var formData = new FormData();

        formData.append('attachment', fileData);

        $http({
            method: 'POST',
            url: 'http://localhost:9000/v1/attachments',
            transformRequest: angular.identity,
            headers: {'Content-Type': false},
            data: formData
        }).success(function (data) {
            console.log('success: ', data);
        });
    }


  }

})();

