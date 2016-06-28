#!/bin/bash
# arrow-detect.sh: 检测方向键, 和一些非打印字符的按键.
# 感谢, Sandro Magi, 告诉了我们怎么做到这点.

# copied from:
# http://blog.csdn.net/dragon101788/article/details/7794853
# bysong

# --------------------------------------------
# 按键所产生的字符编码.
arrowup='\[A'
arrowdown='\[B'
arrowrt='\[C'
arrowleft='\[D'
insert='\[2'
delete='\[3'
# --------------------------------------------

SUCCESS=0
OTHER=65

#set -x

for (( ; ; )) do 
echo -n "Press a direction key...  "
# 如果不是上边列表所列出的按键, 可能还是需要按回车. (译者注: 因为一般按键是一个字符)
read -n3 key                      # 读取3个字符.

key_event=''
echo -n "$key" | grep "$arrowup"  # 检查输入字符是否匹配.
if [ "$?" -eq $SUCCESS ]
then
echo "Up-arrow key pressed."
key_event='KEYCODE_DPAD_UP'
fi

echo -n "$key" | grep "$arrowdown"
if [ "$?" -eq $SUCCESS ]
then
echo "Down-arrow key pressed."
key_event='KEYCODE_DPAD_DOWN'
fi

echo -n "$key" | grep "$arrowrt"
if [ "$?" -eq $SUCCESS ]
then
echo "Right-arrow key pressed."
key_event='KEYCODE_DPAD_RIGHT'
fi

echo -n "$key" | grep "$arrowleft"
if [ "$?" -eq $SUCCESS ]
then
echo "Left-arrow key pressed."
key_event='KEYCODE_DPAD_LEFT'
fi

if [[ "x$key_event"  != "x" ]] ; then
  echo "key_event: $key_event"
  adb shell input keyevent $key_event
fi

done

#  练习:
#  -----
#  1) 使用'case'结构来代替'if'结构,
#+    这样可以简化这个脚本.
#  2) 添加 "Home", "End", "PgUp", 和 "PgDn" 这些按键的检查.


