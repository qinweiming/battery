(function () {
  'use strict';

  angular.module('BlurAdmin.pages.government')
  .controller('DistributionCtrl', DistributionCtrl);

  /** @ngInject */
  function DistributionCtrl($scope, $http, toastr, $filter) {

    $scope.filters={
      "startDate": "",
      "endDate": "",
      "companyName": ""
    };
    
    $scope.showTable = function(){

      $scope.selectComp = '';
      $scope.selectComp =   "'.*" + $scope.filters.companyName + ".*'" +"," + "\'" 
                           + $filter('date')($scope.filters.startDate,'yyyy-MM-dd') + "\'"
                           +","+ "\'" + $filter('date')($scope.filters.endDate,'yyyy-MM-dd') + "\'";
      $scope.newParams = encodeURI($scope.selectComp);
      console.log($scope.newParams);
      $http.get(getCertsApi+ '?params='+$scope.newParams).success(function(data){
        $scope.distributionData = data; 
      });
    };

    var randomData = function(){
      
        return Math.round(Math.random()*1000);

    };

$scope.mapConfig = {
                                theme:'default',
                                
                                dataLoaded:true
        };



$scope.mapOption = {
    title: {
        text: '密度分布',
        subtext: '',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data:['厂家电池密度']
    },
    visualMap: {
        min: 0,
        max: 2500,
        left: 'left',
        top: 'bottom',
        text: ['高','低'],           // 文本，默认为数值文本
        calculable: true
    },
    toolbox: {
        show: true,
        orient: 'vertical',
        left: 'right',
        top: 'center',
        feature: {
            dataView: {readOnly: false},
            restore: {},
            saveAsImage: {}
        }
    },
    series: [
        {
            name: '厂家电池密度',
            type: 'map',
            mapType: 'china',
            roam: true,
            label: {
                normal: {
                    show: true
                },
                emphasis: {
                    show: true
                }
            },
            data:[
                {name: '北京',value: randomData() },
                {name: '天津',value: randomData() },
                {name: '上海',value: randomData() },
                {name: '重庆',value: randomData() },
                {name: '河北',value: randomData() },
                {name: '河南',value: randomData() },
                {name: '云南',value: randomData() },
                {name: '辽宁',value: randomData() },
                {name: '黑龙江',value: randomData() },
                {name: '湖南',value: randomData() },
                {name: '安徽',value: randomData() },
                {name: '山东',value: randomData() },
                {name: '新疆',value: randomData() },
                {name: '江苏',value: randomData() },
                {name: '浙江',value: randomData() },
                {name: '江西',value: randomData() },
                {name: '湖北',value: randomData() },
                {name: '广西',value: randomData() },
                {name: '甘肃',value: randomData() },
                {name: '山西',value: randomData() },
                {name: '内蒙古',value: randomData() },
                {name: '陕西',value: randomData() },
                {name: '吉林',value: randomData() },
                {name: '福建',value: randomData() },
                {name: '贵州',value: randomData() },
                {name: '广东',value: randomData() },
                {name: '青海',value: randomData() },
                {name: '西藏',value: randomData() },
                {name: '四川',value: randomData() },
                {name: '宁夏',value: randomData() },
                {name: '海南',value: randomData() },
                {name: '台湾',value: randomData() },
                {name: '香港',value: randomData() },
                {name: '澳门',value: randomData() }
            ]
        }
    ]
};


    }

})();
