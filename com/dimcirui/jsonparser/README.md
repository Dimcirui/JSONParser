Known issues:
1、When the end element of JsonObject and JsonArray is a number, an error occurs if the number is one of the outermost values.
I.e., it only works if the ending is two or more ']' (or '}').
I guess the problem may be caused by the readNumber() method in Lexer.java.
2、According to the above bug, it can be inferred that the parser can not analyze whether the number of ']'(or '{') at the end matches with '['(or '{'), as long as it is greater than or equal to the number of '[', it can pass the test. 
I think introducing a 'count' variable would solve this problem.

References:
自己动手实现一个简单的JSON解析器 - https://segmentfault.com/a/1190000010998941
如何编写一个JSON解析器 - https://www.liaoxuefeng.com/article/994977272296736
JSON - http://json.org/json-zh.html
Compilers 南京大学软件学院 编译原理 课程视频 - https://space.bilibili.com/479141149/channel/collectiondetail?sid=2312309


