#! /bin/sh

CTIME=$(date +%Y%m%d%H%M)
ROOT_RUNTIME="/www/tronyes/reward"
DIR_BACKUP="${ROOT_RUNTIME}/backup"
DIR_TODO="${ROOT_RUNTIME}/todo"

QuartzTasks="QuartzTasks.jar"

task="${ROOT_RUNTIME}/${QuartzTasks}"
taskTodo="${DIR_TODO}/${QuartzTasks}"
taskBackup="${DIR_BACKUP}/${QuartzTasks}_${CTIME}.jar"

if [ ! -f "${taskTodo}" ]; then
	echo "${taskTodo} 不存在 上线终止"
	exit
fi

pid=`(ps -ef | grep "${ROOT_RUNTIME}/${QuartzTasks}" | grep -v "grep") | awk '{print $2}'`
echo "当前 QuartzTasks 进程: ${pid}"
for id in $pid
do
	kill -15 $id
done


mv $task $taskBackup
cp $taskTodo $task


echo "启动 ${task}"
java -jar $task &

echo "部署完成"
