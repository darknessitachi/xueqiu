@echo off 
:: 删除class目录中的code目录文件
del /Q D:\Documents\GitHub\xueqiu\target\classes\code\*
:: 拷贝源码中的配置文件到class目录
copy /y D:\Documents\GitHub\xueqiu\src\main\java\code D:\Documents\GitHub\xueqiu\target\classes\code
copy /y D:\Documents\GitHub\xueqiu\src\main\java\config D:\Documents\GitHub\xueqiu\target\classes\config
:: 删除class目录中不必要的文件
del D:\Documents\GitHub\xueqiu\target\classes\config\Constants.java
java -classpath "D:\Documents\GitHub\xueqiu\target\classes;C:\Users\Administrator\Documents\Downloads\json-lib\*"  gui.MainGui
exit


