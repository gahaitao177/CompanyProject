编写的本地打包上传jar到storm的脚本
wsl
使用的git.bash来执行.sh文件
其中ssh命令和scp命令时候需要输入密码
将本地公钥加入storm服务器192.168.1.207中，免去每次输入密码的麻烦。
具体操作以本机为例：
1 ,打开git.bash
2 ,cd ~/.ssh中，查看是否存在id_dsa id_dsa.pub 文件
3 ,没有，执行ssh-keygen -t dsa  生成密钥文件id_dsa.pub，（生成密钥中，最好全部回车设置为空）
    注意：或者用ssh-keygen -t rsa 生成 id_rsa.pub
4 ,scp ~/.ssh/id_dsa.pub  root@192.168.1.207:/opt/id_dsa.pub  将id_dsa.pub文件上传到storm服务器上
5 ,进入storm服务器207。
6 ,cat /opt/id_dsa.pub >> ~/.ssh/authorized_keys  将id_dsa.pub追加到authorized_keys中
7 ,rm /opt/id_dsa.pub  完成后将其删除

cd 到nirvana/start目录下
执行user.sh 自动打包并上传模块到storm中部署
