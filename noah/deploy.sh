#! /bin/sh

ROOT_RUNTIME="/www/tronyes/reward"
DIR_TODO="${ROOT_RUNTIME}/todo"

HOSTNAME="47.107.153.157"
USER="tronjar"

QuartzTasks="../build/libs/QuartzTasks.jar"
StartService="start_service.sh"

if [ ! -f "${QuartzTasks}" ]; then
	echo "${QuartzTasks} 不存在 上线终止"
	exit
fi

if [ ! -f "${StartService}" ]; then
	echo "${StartService} 不存在 上线终止"
	exit
fi

# read -s -p "输入 $USER@$HOSTNAME 密码"  PASS

# ==========BEGIN==========
echo "\r清理 $DIR_TODO 文件夹"

TMP_BAK=$(mktemp)

# create expect script
cat > $TMP_BAK << EOF
#exp_internal 1 # Uncomment for debug
set timeout -1
spawn ssh $USER@$HOSTNAME
match_max 100000
expect "Last*"
send -- "rm -rf $DIR_TODO\r"
send -- "mkdir $DIR_TODO\r"
send -- "exit\r"
interact
EOF

expect -f $TMP_BAK
# remove expect script
rm $TMP_BAK
# ==========END===========


# ==========BEGIN==========
echo "开始scp文件"


TMP_BAK=$(mktemp)

# create expect script
cat > $TMP_BAK << EOF
#exp_internal 1 # Uncomment for debug
set timeout -1
spawn scp $QuartzTasks $StartService $USER@$HOSTNAME:$DIR_TODO
match_max 100000
interact
EOF

expect -f $TMP_BAK
# remove expect script
rm $TMP_BAK
# ==========END===========

# ==========BEGIN==========
echo "确定要开始执行部署脚本?(y/n)"
read val
if [ "$val" != "y" ];  then
	exit
fi

TMP_BAK=$(mktemp)

# create expect script
cat > $TMP_BAK << EOF
#exp_internal 1 # Uncomment for debug
set timeout -1
spawn ssh $USER@$HOSTNAME
match_max 100000
expect "Last*"
send -- "chmod +x $DIR_TODO/$StartService\r"
send -- "$DIR_TODO/$StartService\r"
send -- "rm -rf $DIR_TODO/$StartService\r"
send -- "exit\r"
interact
EOF

expect -f $TMP_BAK
# remove expect script
rm $TMP_BAK
# ==========END===========
