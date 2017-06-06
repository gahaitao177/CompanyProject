#! /usr/bin/env python
# -*- coding: utf-8 -*-

import os,sys
import happybase
from kafka import KafkaConsumer
from kafka import KafkaProducer
import json
import time
import threading
import random
import traceback
import urllib2
import hashlib

import time  
from datetime import datetime 

from pykafka import KafkaClient

#is_debug = True
is_debug = False
ip_dic = {}
gps_dic = {}

def getMd5Str(src):
	m2 = hashlib.md5()   
	m2.update(src)   
	return m2.hexdigest()

def getTimeLong(start , end):
        try:
                start = str(start)
                end = str(end)
                t1 = long(start)
                t2 = long(end)

                timeLong = t2 - t1
                if timeLong < 0:
                        return 0              
                else:
                        return timeLong / 1000

        except Exception,e:
                return 0

def getTimeStr(timeLong):
	timeLong = str(timeLong)
	if len(timeLong) < 10:
		return ""       #格式不对
	try:
		t1 = timeLong[:10]
		l1 = long(t1)
		t2 = time.localtime(l1)
		return time.strftime("%Y-%m-%d %H:%M:%S" , t2)
	except Exception,e:
		sys.stderr.write("不正确的时间戳格式:%s" % timeLong)

#根据 lat,lng 查询用户的地域信息
def getLocationByGPS(lat,lng):
	try:
		#保留两位小数
		lat2 = lat[:lat.index(".") + 3]		
		lng2 = lng[:lng.index(".") + 3]
		k = lat2+"#"+lng2
		if k in gps_dic:
			return gps_dic[k]


		time1 = datetime.now()
		baiduURL = "http://api.map.baidu.com/geocoder/v2/?";
		baiduURL += "output=json&ak=lK1NHo9y0yP7E6Ub7VCCnSpcWpMZ6fbC&location=" + lat + "," + lng

		request = urllib2.Request(baiduURL)
		content = urllib2.urlopen(request,timeout=3)
		content = content.read() 

		result = json.loads( content )
		if 0 <> result["status"]:
			return None

		address = result["result"]["addressComponent"]

		location = {}
		location["country"] = address["country"]
		location["region"] = address["province"]
		location["city"] = address["city"]

		if "" == location["country"]:
			return None
		time2 = datetime.now()
		if is_debug:
			print "get gps time:%s" % (time2 - time1).seconds
			print "get location by gps success."
		
		gps_dic[k] = location
	
		return location
		
	except Exception,e:
		
		if is_debug:
			print "get location by gps failed."
			traceback.print_exc()
		return None
			
#根据 IP 查询用户的地域信息
def getLocationByIP(ip):
	try:
	
		if ip in ip_dic:
			return ip_dic[ip]
		
		time1 = datetime.now() 
		taobaoURL = "http://ip.taobao.com/service/getIpInfo.php?";		
		taobaoURL += "ip="+ip

		request = urllib2.Request(taobaoURL)
		content = urllib2.urlopen(request,timeout=3)
		content = content.read()

		result = json.loads( content )

		address = result["data"]

		location = {}
		
		location["country"] = address["country"]
		location["region"] = address["region"]
		location["city"] = address["city"]

		time2 = datetime.now() 
		
		if is_debug:
			print "get ip time:%s" % (time2 - time1).seconds
			print "get location by ip success."

		ip_dic[ip] = location

		return location

	except Exception,e:
	
		if is_debug:
			print "get location by ip failed."
			traceback.print_exc()

		return None 


#根据 GPS 和 IP 获取用户地域信息,优先使用 GPS
def getLocation(lat , lng , ip ):
	if is_debug:
		print "area info:%s , %s , %s" % (lat , lng , ip)
	location = None
	if lat <> "" and lng <> "":
		location = getLocationByGPS( lat , lng)

		if location == None:
			location = getLocationByIP( ip ) 
	else:
		location = getLocationByIP( ip )

	return location



########################################

def etl_one_msg(report_time , dic):
	#缺省的 key 补全
	keys = ["appGps" , "userId" , "appKey" , "sdkVersion" , "appIp" , "deviceOs" , "deviceModel" , "deviceBrand" , "appName" , "appInstallPkgSource" , "appVersion" , "deviceRes" , "deviceType" , "deviceId" , "appNetWork" , "appChannel" , "appSource" , "pkgId" , "osVersion" , "mac" , "androidId" , "imei" , "idfa" , "clientId" , "openudid" , "SimulateIDFA"]

	for k in keys:
		if k not in dic:
			dic[k] = ""


	#1 补充上报时间
	report_time = getTimeStr( report_time )
	dic["reportTime"] = report_time

	#2 处理 客户 的地域
	lat = ""
	lng = ""
	ip  = dic["appIp"]
	try:
		lat = dic["appGps"].split(",")[1]
		lng = dic["appGps"].split(",")[0]
	except Exception,e:
		#traceback.print_exc()
		pass
	location = getLocation(lat , lng , ip)
	if location == None:
		dic["country"] 	= ""
		dic["region"] 	= ""
		dic["city"] 	= ""
	else:
		dic["country"]  = location["country"]
		dic["region"] 	= location["region"]
		dic["city"]  	= location["city"]

	if is_debug:
		print "area:%s %s %s" %(dic["country"] , dic["region"] , dic["city"])

	#3 格式化启动事件
	starts = []
	start_keys = ["ios_start" , "androidStart"]
	if "events" in dic:
		#增加字符串格式的事件触发时间
		events = dic["events"] 
		for event in events:
			event["timeStr"] = getTimeStr( event["time"] )
			if event["eventId"] in start_keys:
				start = {}
				start["time"] = getTimeStr( event["time"] )
				starts.append( start )
	dic["starts"] = starts
			

	#4 增加页面访问时长
	if "histories" in dic:
		histories = dic["histories"]
		for h in histories:
			h["enterTimeStr"] 		= getTimeStr( h["enterTime"] )
			h["exitTimeStr"] 		= getTimeStr( h["exitTime"] )
			h["pageViewTimeLong"] 	= getTimeLong( h["enterTime"] , h["exitTime"] )
			
	#5 对 clientId 进行 MD5 编码
	client_id = dic["clientId"]
	if client_id == "":
		dic["clientIdMd5"] = ""
	else:
		dic["clientIdMd5"] = getMd5Str( client_id ) 
	
	#6 格式化 deviceType , 0:android   1:ios
	if str( dic["deviceType"] )  ==  "0":
		dic["deviceType"] = "android"
	elif str( dic["deviceType"] )  ==  "1":
		dic["deviceType"] = "ios"

	#7 补全 sdkVersion
	if "" == dic["sdkVersion"]:
		if dic["deviceType"] == "android":
			dic["sdkVersion"] = "android-0.0.5"
		else:
			dic["sdkVersion"] = "ios-0.0.5"
	
	return dic
	

#etl 旧数据
def etl_msg(report_time , msg):
	result_list = []
	#将 json 格式的消息转换为数组
	list = json.loads( msg)
	for dic in list:
		if dic == None:
			continue
		tmp = None
		try:
			tmp = etl_one_msg(report_time , dic)
		except Exception,e:
			traceback.print_exc()
		if tmp <> None:
			result_list.append( tmp )

	return result_list

########################################

class ETL(threading.Thread):
	def __init__(self, num):
		threading.Thread.__init__(self)  
		#线程号
		self.num = num

	
		#kafka 消费者
		self.consumer = KafkaConsumer("nirvana_analyses_data_upload_topic",bootstrap_servers="192.168.83.26:9475,192.168.83.25:9840,192.168.83.22:9674",group_id='client_data_etl')
		#kafka 生产者
		self.producer = KafkaProducer(bootstrap_servers = "192.168.83.26:9475,192.168.83.25:9840,192.168.83.22:9674") 
		#etl 后的 kafka topic
		#self.topic_after_etl = "test_xiaxc"
		self.topic_after_etl = "youyu_mobile_data_after_etl"
	
	def run(self):
		for msg in self.consumer:
			try:
				#etl 旧消息,返回处理后的数组
				_time_step_1 = datetime.now()  			
				msg_after_etl = etl_msg(str(msg.timestamp) , msg.value)
				_time_step_2 = datetime.now() 
				
				if is_debug:
					print "etl time:%s" % (_time_step_2 - _time_step_1).seconds  
				for one in msg_after_etl:
					#发送 etl 后的消息,注意 json 不识别单引号,需要替换成双引号
					self.producer.send(self.topic_after_etl , json.dumps( one ).encode('utf-8') )
				_time_step_3 = datetime.now()
				
				if is_debug:
					print "produce time:%s" % (_time_step_3 - _time_step_2).seconds  
					print "="*20
				self.producer.flush()
				sys.stdout.flush()

				self.consumer.commit_async()  
			except Exception,e:
				traceback.print_exc()

		self.consumer.close()

def main():
	#启动 N 个线程并发进行 ETL,多开放5个消费者,防止部分消费者异常退出
	for i in range(25):
		etl = ETL(i)
		etl.start()
		time.sleep(1)


if __name__ == '__main__':
	main()
