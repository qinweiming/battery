/**
 * @author v.lugovsky
 * created on 16.12.2015
 */
(function () {
  'use strict';

    

  angular.module('BlurAdmin.pages.myNewPage')
      .controller('newTablesPageCtrl', newTablesPageCtrl);

  /** @ngInject */
  function newTablesPageCtrl($scope, $filter, $http, editableOptions, editableThemes) {



  	jQuery.get("http://localhost:3003/posts").success(function(data){
  	   
        $scope.newSmartTableData = data ;
       
      
      
    }).error(function(){
    	
        alert("an unexpected error ocurred!");
    });

    $scope.newSmartTablePageSize = 5;

    
  


      $scope.shenpiyes = function() {
        

       
       
      for(var i=0; i<document.getElementsByName("chk").length;i++){
        
             if(document.getElementsByName("chk")[i].checked == true){//得到选中的单选按钮如果要得到值 那么可以：

                                                                       //alert("选择成功！");
                var array = {};
                array = $scope.newSmartTableData;
                 for(var i=0; i<document.getElementsByName("chk").length;i++){
                     if(document.getElementsByName("chk")[i].checked == true){
                         for(var j=0;j<$scope.newSmartTableData.length;j++){
                             if(document.getElementsByName("chk")[i].value == array[j]){
                                 array.remove(j);

                             }
                             
                         }
                         $scope.newSmartTableData = array;
                             $http.put("http://www.localhost:3003/posts").success(function(data){  //put方法有问题
                                 data = $scope.newSmartTableData;
                             }).error(function(){
                                 alert("修改数据失败!");
                             });
                     }
                 }
                                                                     }else{
                                                                      //alert("选择失败");
                                                                     }
                                                                   } 
//$scope.shenpiyes();
                                                                  

                                                                
                                                                };

$scope.shenpino =function(){
$scope.shenpino();
    };

    

    

    editableOptions.theme = 'bs3';
    editableThemes['bs3'].submitTpl = '<button type="submit" class="btn btn-primary btn-with-icon"><i class="ion-checkmark-round"></i></button>';
    editableThemes['bs3'].cancelTpl = '<button type="button" ng-click="$form.$cancel()" class="btn btn-default btn-with-icon"><i class="ion-close-round"></i></button>';


  }

})();
