#!/usr/bin/env bash
scp root@192.168.83.40:/opt/cluster/storm/log4j2/worker.xml worker.xml
scp worker.xml root@192.168.83.40:/opt/cluster/storm/log4j2/worker.xml
scp worker.xml root@192.168.83.37:/opt/cluster/storm/log4j2/worker.xml
scp worker.xml root@192.168.83.38:/opt/cluster/storm/log4j2/worker.xml
scp worker.xml wujiahua@192.168.83.65:/opt/cluster/storm/log4j2/worker.xml
scp worker.xml wujiahua@192.168.83.56:/opt/cluster/storm/log4j2/worker.xml
