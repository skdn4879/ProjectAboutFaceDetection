import cv2
import numpy as np
import os
from bluetooth import *
from time import sleep
from pyfcm import FCMNotification
import subprocess
import datetime

def clearImageFolder():
    path = '/home/pi/Graduation/imageForStorage'
    os.system('rm -rf %s/*' % path)

def calImageName():
    nowDatetime = datetime.datetime.now()
    koreaTime = nowDatetime + datetime.timedelta(hours=8)
    strTime = str(koreaTime)
    times = strTime.split('.')
    nowTime = times[0]
    newTimes = nowTime.split(':')
    uploadImageName = newTimes[0] + '-' + newTimes[1] + '-' + newTimes[2] + ".jpg"
    return uploadImageName

def clearAboutScore():
    global countStack
    global totalScore
    countStack = 0
    totalScore = 0

def restCamProcessForOwner():
    global cam
    cam.release()
    clearAboutScore()
    sleep(5)
    cam = cv2.VideoCapture(streamUrl)
    
def restCamProcessForOther():
    global cam
    cam.release()
    cv2.destroyWindow('camera')
    clearAboutScore()
    sleep(30)
    cam = cv2.VideoCapture(streamUrl)

def ownerProcess(accuracy):
    if(accuracy > 70):
        socket = BluetoothSocket(RFCOMM)
        socket.connect(("98:DA:60:01:70:0C", 1))
        msg = '3'
        socket.send(msg)
        socket.close()
        print("Door Open")
        restCamProcessForOwner()

def sendPushMessage():
    global cam
    APIKEY = "AAAA46h66HM:APA91bHWqXn6WCTRFnUGcSN92IQ8-_dyZJSAQn1c5zxUKjHQMxbaY0ou3hjnNTS7VR5v-erS765fYTR-t6-YF_UTJjISe8482AlHqOnj9yuR9f23BzO_SxXGIl5hgzyh4P54YAvtotuX"
    TOKEN = "c0TeAF5tR725-0_gCHRC22:APA91bGxMAeSld2uJ4Vnym9GXsLaqdkwdUpOP6NzY-2RIlAHwn3ekcndy7ipecskxj2FyMY3fIB0XcqMXsSzr9C6og1SS2cdYeAUfTIXHT1Lqx_j6bTavDL7-gafJk7SuLWqtPKoZa39"
    push_service = FCMNotification(APIKEY)
    data_message = {
        "contents": "Someone Came to your House",
        "title": "House Notice"
        }
    push_service.single_device_data_message(registration_id=TOKEN, data_message=data_message)
    clearAboutScore()
    clearImageFolder()
    imageName = calImageName()
    ret, img = cam.read()
    cv2.imwrite('/home/pi/Graduation/imageForStorage/' + imageName, img)
    subprocess.call('python3 /home/pi/Graduation/PutFirebase.py', shell=True)
    restCamProcessForOther()

recognizer = cv2.face.LBPHFaceRecognizer_create()
recognizer.read('trainer/faceTrainer5.yml')
cascadePath = "haarcascades/haarcascade_frontalface_default.xml"
faceCascade = cv2.CascadeClassifier(cascadePath)
font = cv2.FONT_HERSHEY_SIMPLEX
streamServerAddress = '192.168.0.21'

#iniciate id counter
id = 0

#names related to ids: example ==> owner: id=1, etc
# Add every user name like this
names = ['None', 'Owner']
streamUrl = 'http://'+streamServerAddress+':8080/stream/video.mjpeg'

#Initialize and start realtime video capture
cam = cv2.VideoCapture(streamUrl)
cam.set(3, 640)
cam.set(4, 480)

#Define min window size to be recognized as a face
minW = 0.1*cam.get(3)
minH = 0.1*cam.get(4)

countStack = 0
totalScore = 0

while True:
	ret, img = cam.read()
	#img = cv2.flip(img, -1)
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

	faces = faceCascade.detectMultiScale(
		gray,
		scaleFactor = 1.2,
		minNeighbors = 5,
		minSize = (int(minW), int(minH))
	)

	for(x, y, w, h) in faces:
		cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)
		id, confidence = recognizer.predict(gray[y:y+h, x:x+w])
		countStack += 1
		totalScore += confidence
		showConfidence = " {0}%".format(round(100 - confidence))
		print(showConfidence)

		#Check if confidence is less them 100 ==> "0" is perfect match
		if(countStack == 5 and (totalScore // 5) < 30):
			id = names[id]
			accuracy = (round(100 - confidence))
			ownerProcess(accuracy)
			
		elif(countStack == 5 and (totalScore // 5) > 30):
			id = "Unknown"
			#sendPushMessage()
			
		

		#cv2.putText(img, str(id), (x+5, y-5), font, 1, (255,255,255),2)
		#cv2.putText(img, str(confidence), (x+5, y+h-5), font, 1, (255,255,0), 1)

	cv2.imshow('camera', img)
	cv2.moveWindow('camera', 300, 150)
	k = cv2.waitKey(10) & 0xff
	if k == 27:
		break

print("\n [INFO] Exiting Program and cleanup stuff")
cam.release()
cv2.destroyAllWindows()
