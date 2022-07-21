本项目为CSU2022应用基础实践一聊天室课程设计。

# 一、开发工具及环境说明

**开发工具**：IntelliJ IDEA集成开发环境，JDK版本为JDK 16.0.2，在Windows 11操作系统下进行开发

**环境说明**：采用Java SE 基础模块进行开发，项目的运行依赖JVM(Java Virtual Machine，Java虚拟机)，项目依赖的Jar包有：录音所需Jar包jl1.0.jar，连接mySql数据库所需的Jar包mysql-connector-java-8.0.28.jar。

**注**：相关网络通信需要系统许可，录音需要系统允许使用麦克风，主机系统中应提前预置mySql数据库，并进行初始的建表操作，详见4.3节。

# 二、网络聊天程序业务分析

## 2.1 相关业务分析

  本程序由服务器端和客户端两部分组成。程序开始时，服务器首先开放端口，连接数据库并接受客户端连接。服务器端会显示当前在线的用户列表，群发消息框、远程关闭按钮和群发消息按钮。通过“远程关闭”按钮，可以强制某些用户下线；通过“群发消息”按钮，可以群发消息到各个在线客户端。

当每个客户端进行连接时，会首先跳出登录界面；当用户是首次使用本系统时，可以进行注册，即点击“注册”按钮，客户端输入帐号、密码和确认密码进行注册。客户还可以选取自己的头像，当没有选择头像时，系统将会选用默认头像作为他的头像。当客户端提交注册信息时，服务器端将会进行以下的判断条件：若帐号被注册，则拒绝本次注册，否则判断密码和确认密码是否相同；若相同则同意本次注册，并将帐号和密码信息存储于数据库中；若不相同则拒绝本次注册。注册完的账户当进行再次登录系统时，无需注册，直接登录即可。客户端进行登录时，首先输入帐号和密码，并点击“登录”，服务器端通过数据库查询操作判断帐号是否存在、帐号和密码信息是否对应，若存在并对应，再判断用户是否重复登录，若非重复登录则同意登录，开放文件传输端口并与客户端连接，同时将帐号和ChatThread类对象的对应关系记录于HashMap中，以便后续添加、删除成员和聊天记录的转发，否则任意一种情况都将拒绝登录。

  登录完成后进入聊天室主页面，包含聊天记录框、聊天文字发送框、发送文字、发送语音、发送文件、好友列表、客户账号、客户头像、系统时间、离开聊天室等组件。

当客户端输入文字并点击“发送文字”按钮时，客户端将把文字和帐号发送到服务器端，并清空发送框；服务器获取文字和昵称，再转发到每个上线的客户端。

当客户端进行语音聊天时，客户端点击“发送语音”按钮，将弹出录音窗口，点击“开始录音”；当客户端点击“结束录音”时，客户端将会把帐号和录音文件发送到服务器端；服务器端接收录音，并将录音转发给除了录音客户端以外的其他客户端，客户端接收录音后，会弹出“[语音接收提醒]”，并选择是否接收；若同意接收，则会播放服务器端传入的由录音客户端发来的语音，否则将会丢弃该语音。

当客户端进行传送文件时，客户端点击“发送文件”按钮，客户端会选取系统中的文件并发送给服务器，服务器端将文件转发给除了发送文件客户端以外的其他客户端，客户端接收文件后，会弹出“[文件接收提醒]”，并选择是否接收；若同意接收，则会打开服务器端传入的由文件客户端发来的文件，否则将会丢弃该文件，文件的类型可为txt、pdf、docx等。

当客户端想要查看系统时间时，点击“系统时间”按钮即可，可以校对系统时间；当客户端想要离开聊天室时，点击“离开聊天室”，客户端将会给服务器发送离开聊天室的标记，服务器接收到后会同意请求，并为其他各客户端发送其离开聊天室的记录，客户端会将其显示在聊天记录框中。

## 2.2 相关业务流程图

2.1所述的相关业务的流程图如下图所示。

（1）注册、登录业务：

​    ![image-20220721193728934](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193728934.png)                           

（2）语音聊天业务：

 ![image-20220721193732743](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193732743.png)

（3）传送文件业务：

 ![image-20220721193737728](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193737728.png)

（4）离开聊天室业务：

 ![image-20220721193743026](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193743026.png)

# 第三章  网络聊天程序系统设计

## 3.1 系统功能定义

### 3.1.1 系统实现的功能描述

1.用Java图形用户界面编写聊天室服务器端和客户端，支持多个客户端连接到一个服务器。每个客户端能够注册并登录账号。

\2. 可以实现群聊（聊天记录显示在所有客户端界面）。

\3. 完成好友列表在各个客户端上显示。

\4. 可以发送语音，实现语音聊天。

\5. 可以发送文件，实现文件传输。

\6. 可以实现私人聊天，用户可以选择某个其他用户，单独发送信息、发送语音、发送文件，并可以实现窗口抖动。

\7. 服务器能够群发系统消息，能够强行让某些用户下线。

\8. 客户端的上线下线要求能够在其他客户端上面实时刷新，客户端可以查看系统时间。

### 3.1.2 消息头部标记的定义

服务器与客户端进行通信需要传输相关的消息，为了使服务器与客户端之间消息能够正确、有效的转达，设计不同的消息头部标记是非常重要的。下面的表格描述了本项目中不同头部标记的定义。

| 消息头部      | 具体含义                             |
| ------------- | ------------------------------------ |
| REG1          | 检查注册时密码和确认密码一致性       |
| YES           | 注册密码和确认密码一致               |
| NO            | 注册信息有误或登录信息有误           |
| REG2          | 检查注册时用户名是否已经被注册       |
| EXISTS        | 用户名已经存在                       |
| INSERT        | 用户名已经成功注册                   |
| LOGIN         | 用户登录                             |
| CHONG         | 用户重复登录，登录失败               |
| NO            | 用户不存在或输入信息不正确，登录失败 |
| NEW           | 新用户登入聊天室                     |
| USER          | 服务器发送好友列表                   |
| RUN           | 客户端离开聊天室                     |
| SILIAO        | 客户端请求私聊                       |
| ACCEPT/REFUSE | 同意/拒绝私聊                        |
| SI            | 客户端发送私聊消息                   |
| SIMESSAGE     | 服务器转发的客户端私聊消息           |
| SID           | 发送私聊窗口抖动                     |

| SID  | 发送私聊窗口抖动 |
| ---- | ---------------- |
|      |                  |

## 3.2 类的结构设计

（1）服务器端：

![image-20220721193836831](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193836831.png)   

（2）客户端：

 ![image-20220721193846403](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193846403.png)

## 3.3 相关数据库初始化操作

进入mysql，使用语句mysql -u root -p，并进入login数据库，use login;

创建用户表：

```
create table client(
   id int(11) primary key auto_increment,
   username varchar(20) not null,
   password varchar(20) not null,
   picture_path varchar(200) not null
 )comment '用户表';
```

建表完成后，用户表的结构如图所示：

![image-20220721193914948](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193914948.png)        

由此，初始建表操作完成。注册和查询操作将在该表中进行。

# 四、聊天程序运行结果与测试分析

## 4.1 程序运行结果

服务器运行界面：

![](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721193952854.png)                       

客户端登录页面：

 ![image-20220721194006220](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194006220.png)

客户端注册页面：

 ![image-20220721194011681](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194011681.png)

注册时选取头像（若未选取将使用系统默认头像）并提交注册：

 ![image-20220721194015867](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194015867.png)



 ![image-20220721194020218](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194020218.png)

若注册账号已经被注册：

 ![image-20220721194024267](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194024267.png)

若登录账号或密码错误：

![image-20220721194057980](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194057980.png)

若该用户已经登录：

 ![image-20220721194102463](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194102463.png)

客户端进入聊天室界面：

 ![image-20220721194106005](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194106005.png)

客户端发送文字：

 ![image-20220721194109853](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194109853.png)

客户端发送语音：

 ![image-20220721194113789](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194113789.png)

客户端发送文件：

 ![image-20220721194118182](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194118182.png)

![image-20220721194129622](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194129622.png)

客户端查看系统时间：

 ![image-20220721194133645](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194133645.png)

客户端发送私聊请求：

 ![image-20220721194137831](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194137831.png)

![image-20220721194148298](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194148298.png)

客户端发送窗口抖动：

 ![image-20220721194153115](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194153115.png)

服务器远程关闭客户端：

 ![image-20220721194158037](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194158037.png)

服务器发送全体消息（群发消息）：

![image-20220721194202423](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/image-20220721194202423.png) 

## 4.2 程序测试分析

- 正确性：能够正确的实现客户端和服务器的通信，可以实现不同客户端群聊、私聊的功能，可以正确处理发送文字、发送语音、发送文件等场景需求，以及私聊间窗口抖动的功能。本系统具备正确性。

- 健壮性：系统实现了对异常操作的处理。若前端输入非法或未定义的信息，不会影响程序其他功能的正常运行。本系统具备健壮性。