import json
import pyrebase
import datetime
import os

with open("/home/pi/Graduation/auth.json") as f:
    config = json.load(f)
    
firebase = pyrebase.initialize_app(config)
db = firebase.database()

directory = '/home/pi/Graduation/imageForStorage'
files = os.listdir(directory)

nowDatetime = files[0].split('.')
textTimes = nowDatetime[0].split(' ')
times = textTimes[1].split('-')
textTime = textTimes[0] + ' ' + times[0] + ':' + times[1] + ':' + times[2]
name = "Other"

dataForPutFirebase = {"name":name, "time":textTime}
db.child("contacts").push(dataForPutFirebase)

uploadFileName = os.path.join(directory, files[0])

storage = firebase.storage()
storage.child("contactsImage/" + files[0]).put(uploadFileName)
