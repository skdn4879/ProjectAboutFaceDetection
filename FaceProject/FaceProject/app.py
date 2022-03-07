from flask import Flask, render_template, Response
from picamera import PiCamera

app = Flask(__name__)

@app.route('/')
def index():
	return render_template('login.html')

@app.route('/video_feed')
def video_feed():
	camera = PiCamera()
	camera.start_preview()
	sleep(5)
	camera.stop_preview()

if __name__ == '__main__':
	app.run(host='0.0.0.0', debug=True, threaded=True)
