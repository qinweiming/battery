## 项目结构   
- frontend    
Web前端，Angular JS 1.x , Bootstrap 3.x
- frontend   
基于BlurAdmin布局框架的web前端,Angular JS 1.x , Bootstrap 3.x 等 
- android   
Android App , Native 开发
- service   
后端服务, Play 1.4.x , MongoDB 3.2+
- blockchain    
区块链服务，Play 1.4.x
- security   
安全模块，证书与密钥生成，供app和service使用。

## Frontend 模块
使用 [yo angular generator](https://github.com/yeoman/generator-angular) 来生成的基本的Angular与Bootstrap整合的开发脚手架.
- Install & config
 ```
 $ npm install -g cnpm --registry=https://registry.npm.taobao.org
 $ cnpm install -g grunt grunt-cli
 $ cd frontend
 $ cnpm install
 $ cnpm install -g bower
 $ bower install
 ```
 如果提示权限错误，需要用sudo或者管理员身份运行

- Build & development


Run `grunt` for building and `grunt serve` for preview.

## Frontend-frame 模块  
基于[BlurAdmin](http://akveo.com/blur-admin/)布局框架的web前端脚手架。

- Install & config      
 ```
 $ npm install -g cnpm --registry=https://registry.npm.taobao.org
 $ cnpm install  gulp 
 $ cd frontend
 $ cnpm install
  
 ```
 如果提示权限错误，需要用sudo或者管理员身份运行

- Build & development    


Run `gulp` for building 
Run `gulp serve` for preview and develop.



## Service 模块
- 框架说明
  - controller放到controllers.v1 package中，并从 controllers.api.API 继承, 参考Users controller；
  - models 从 play.modules.jongo.BaseModel 继承参考User model;
  - docs 下是 api 文档规范，为swagger格式,用浏览器打开 http://localhost:9000 ；
- 开发和运行
```shell
mongod --dbpath=./data
play deps
play idea
play run

open service.ipr(MacOS)
用IDEA打开service.ipr (Windows)

```



