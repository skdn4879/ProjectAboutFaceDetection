import os
import cv2

faceCascade = cv2.CascadeClassifier('haarcascades/haarcascade_frontalface_default.xml')
streamUrl = 'http://192.168.0.19:8080/stream/video.mjpeg'
cap = cv2.VideoCapture(streamUrl)
cap.set(3, 640)
cap.set(4, 480)

#For each person, enter one numeric face id
face_id = input('\n Write User id end press <return> :')
print("\n [INFO] Initializing face capture. Look the camera and wait")
count = 2000 #initialize individual sampling face count, Please Check dataset Folder in same dir
limitCount = 1000 #Limit Number of Capture Face
stack = 0 #Number For Exit Capture

while True:
	ret, img = cap.read()
	#img = cv2.flip(img, -1)
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	faces = faceCascade.detectMultiScale(gray, 1.3, 5)

	for(x,y,w,h) in faces:
		cv2.rectangle(img,(x,y),(x+w, y+h),(255,0,0),2)
		count += 1
		stack += 1
		# save the captured image into the dataset folder
		cv2.imwrite("facedataset2/User." + str(face_id) + '.' + str(count) + ".jpg", gray[y:y+h, x:x+w])
		cv2.imshow('image', img)

	k = cv2.waitKey(30) & 0xff
	if k == 27:
		break
	elif stack >= limitCount: #every take 50 face sample then stop camera
		break

cap.release()
cv2.destroyAllWindows()
