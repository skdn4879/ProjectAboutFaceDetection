import socket

HOST = ""
PORT = 8888

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
print("socket created")

s.bind((HOST, PORT))
print("socket bind complete")

s.listen(1)
print("socket now listening")

#Pi control function
def do_stuffs(input):
	return_string = "None"

	if(input == "Led1"):
		return_string = "LED1 Doing"
	elif(input == "Led2"):
		return_string = "LED2 Doing"
	elif(input == "Door"):
		return_string = "Door Doing"
	elif(input == "Circular"):
		return_string = "Circular Doing"

	return return_string

while True:
	#Connetion Confirm
	conn, addr = s.accept()
	print("Connect by :", addr)

	#Data receive
	data = conn.recv(1024)
	data = data.decode("utf8").strip()
	if not data:
		break
	print("Receive :" + data)

	#Control Received Data With Pi
	res = do_stuffs(data)
	print("Pi Action: " + res)

	#Send Answer To Client
	conn.sendall(res.encode("utf-8"))

	#Close Connection
	conn.close()
s.close()
