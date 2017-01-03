### Frontend Framework
This project is generated with [BlurAdmin Angular  admin panel front-end framework](http://akveo.com/blur-admin/)
version 0.15.1.

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

  - 需要从后台获取数据时，使用[json-server](https://github.com/typicode/json-server) 来模拟后台;
  - 需要向后台上传文件时，使用[mock-server](https://gist.github.com/UniIsland/3346170)来模拟，由于文件会上传到mock-server运行时的文件夹，因此需要将mock-server.py复制到一个临时文件夹下再运行;
```
$ cnpm install -g json-server
$ 创建db.json 文件 并保存
$ json-server --watch db.json
$ mkdir tmp
$ cp mock-server.py tmp
$ cd tmp 
$ python mock-server.py
```
- Refenrence    

Customizable admin panel framework made with :heart: by [Akveo team](http://akveo.com/). Follow us on [Twitter](https://twitter.com/akveo_inc) to get latest news about this template first!

- Demo    
**[Mint version demo](http://akveo.com/blur-admin-mint/)**             |  **[Blur version demo](http://akveo.com/blur-admin/)**
:-------------------------:|:-------------------------:
![Mint version demo](http://i.imgur.com/A3TMviJ.png)  |  ![Blur version demo](http://i.imgur.com/EAoiK2O.jpg)


- Documentation  
Installation, customization and other useful articles: https://akveo.github.io/blur-admin/

