(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
      .controller('ApproveCtrl', ApproveCtrl);

  /** @ngInject */
  function ApproveCtrl($scope, $http, toastr) {
    var getCertsApi = "http://localhost:3003/certs";
    $scope.approveList = [];
    $scope.checked = [];
    $scope.filters = {
      "startDate": "",
      "endDate": "",
      "companyName": ""
    };

    //获取证书列表数据
    $http.get(getCertsApi)
      .success(function(data){
        $scope.approveList = data;
      });

    //查询

    $scope.search = function(){ 

      $http.get(getCertsApi,{params:$scope.filters}).success(function(data){
        $scope.approveList = data; 
      });

    },

   

    $scope.approveSuccess = function() {
      //创建两个approveList的副本，一个改变其中的status属性的值，
      //另外一个删除对应项，保证在成功执行post操作之后更新视图
      var array = new Array();
      var arrayCopy = new Array();
      array = $scope.approveList;
      arrayCopy = array.concat();
      //判断是否选中了数据
      if($scope.checked.length == 0){
         alert("请至少选择一项数据");
      }else{
      //遍历数组中的元素，将选中的元素的status属性的值改为"1"（审批通过）
      for(var j=0;j<$scope.checked.length;j++){
        for(var i=0;i<$scope.approveList.length;i++){
          if(array[i].id == $scope.checked[j]){
                arrayCopy.splice(i,1);
                array[i].status = "1";
          }
        }
      }
      //将更改完的数据post到后台,在本地测试时新建了一个空的json文件，可以成功写入
      var postCertsApi = "http://localhost:3004/certs";
      $http.post(postCertsApi, array)
        .success(function(data){
          $scope.approveList = arrayCopy;
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
      
  
    },

    $scope.approveFail = function(){
      //创建两个approveList的副本，一个改变其中的status属性的值，
      //另外一个删除对应项，保证在成功执行post操作之后更新视图
      var array = new Array();
      var arrayCopy = new Array();
      array = $scope.approveList;
      arrayCopy = array.concat();
      //判断是否选中了数据
      if($scope.checked.length == 0){
        alert("请至少选择一项数据");
      }else{
        //遍历数组中的元素，将选中的元素的status属性的值改为"2"（审批不通过）
      for(var j=0;j<$scope.checked.length;j++){
        for(var i=0;i<$scope.approveList.length;i++){
          if(array[i].id==$scope.checked[j]){
            arrayCopy.splice(i,1);
            array.ststus = "2";
          }
        }
      }
      //将更改完的数据post到后台，,在本地测试时新建了一个空的json文件，可以成功写入
      var postCertsApi = "http://localhost:3004/certs";
      $http.post(postCertsApi, array)
        .success(function(data){
          $scope.approveList = arrayCopy;
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
    },

//全选
    $scope.selectAll = function () {
        if($scope.select_all == true) {
            $scope.checked = [];
            angular.forEach($scope.approveList, function (item) {
                item.checked = true;
                $scope.checked.push(item.id);
            })
        }else {
            angular.forEach($scope.approveList, function (item) {
                item.checked = false;
                $scope.checked = [];
            })
        }
        console.log($scope.checked);
    },

//单选
    $scope.selectOne = function () {
        $scope.checked = [];
        angular.forEach($scope.approveList , function (item) {
            var index = $scope.checked.indexOf(item.id);
            if(item.checked && index == -1) {
                $scope.checked.push(item.id);
            } else if (!item.checked && index != -1){
                $scope.checked.splice(index, 1);
            };
        })

        if ($scope.approveList.length == $scope.checked.length) {
            $scope.select_all = true;
        } else {
            $scope.select_all = false;
        }
        console.log($scope.checked);
    };

  }



})();

