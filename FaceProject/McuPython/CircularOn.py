from bluetooth import *

socket = BluetoothSocket(RFCOMM)
socket.connect(("98:DA:60:01:70:0C", 1))

msg = '4'
socket.send(msg)

socket.close()
