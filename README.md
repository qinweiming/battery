## 项目结构
- frontend
Web前端，Angular JS 1.x , Bootstrap 3.x
- android
Android App , Native 开发
- service
后端服务, Play 1.4.x , MongoDB 3.2+
- blockchain
区块链服务，Play 1.4.x
- security
安全模块

## Frontend 模块
This project is generated with [yo angular generator](https://github.com/yeoman/generator-angular)
version 0.15.1.

- Build & development

Run `grunt` for building and `grunt serve` for preview.

- Testing

Running `grunt test` will run the unit tests with karma.

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



