(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
      .controller('ApproveCtrl', ApproveCtrl);

  /** @ngInject */
  function ApproveCtrl($scope, $http) {
    var getCertsApi = "http://localhost:3003/certs";
    $scope.approveList = [];
/*    $scope.filterData = {
      "startDate": '',
      "endDate": '',
      "companyName": ''
    };*/

    //获取证书列表数据
    $http.get(getCertsApi)
      .success(function(data){
        $scope.approveList = data;
      });

    //查询
    $scope.filter = function(){
      var getCertsApi2 = "http://localhost:3003/certs";
      
      $scope.filters = {
      "startDate": '$scope.filterData.startDate',
      "endDate": '$scope.filterData.endDate',
      "companyName": '$scope.filterData.companyName'
      };

    
      $http.get(getCertsApi2,$scope.filters).success(function(data){
        $scope.approveList = data;
      });

    },

    $scope.chk = false;
    var num = 0;
    var ide = new Array();
    
    $scope.check = function(item,chk){
      if(chk == true){
        ide[num] =item;
        $scope.number = ide;
        num= num+1;
        $scope.count= num;
        alert("存储成功");
      }else{
        ide[num-1]="";
        $scope.number = ide;
        num= num-1;
        $scope.count = num;
        alert("选择失败");
      }
       
    },

    $scope.checksuccess = function() {
      var array = new Array();
      array = $scope.approveList;
      var getCertsApi3 = "http://localhost:3003/certs";
      for(var j=0;j<$scope.count;j++){
        for(var i=0;i<$scope.approveList.length-1;i++){
          if(array[i] == $scope.number[j]){
                /*array.splice(i,1);*/
                array[i].status = "1";
          }
        }
      }
      
      //将修改之后的发送给
      $http.put(getCertsApi3,array).success(function(data){
        alert("审批操作成功");
      }).error(function(data){
        alert("审批操作失败");
      });
       
     /*$scope.filters = {
      "startDate": '',
      "endDate": '',
      "companyName": '',
      "status": '0'
    };

    
    $http.get(getCertsApi3,$scope.filters)
      .success(function(data){
        $scope.approveList = data;
      });*/
    },

    $scope.checkfail = function(){
      var array = new Array();
      array = $scope.approveList;
      var getCertsApi = '';
      for(var j=0;j<$scope.count;j++){
        for(var i=0;i<$scope.approveList.length-1;i++){
          if(array[i] == $scope.number[j]){
                /*array.splice(i,1);*/
                array.status = 2;
          }
        }
      }
      $http.put(getCertsApi3,array).success(function(data){
        alert("审批操作成功");
      }).error(function(data){
        alert("审批操作失败");
      });
       
     $scope.filters = {
      "startDate": '',
      "endDate": '',
      "companyName": '',
      "status": '0'
    };

    $http.get(getCertsApi3,$scope.filters)
      .success(function(data){
        $scope.approveList = data;
      });
    };

  }



})();

