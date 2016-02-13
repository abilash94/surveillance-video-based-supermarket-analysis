import socket
import time

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# Connect the socket to the port where the server is listening
server_address = ("127.0.0.1", 10006)
sock.connect(server_address)
while True:
	print "sending"
	sock.send("dhjaskhd\n")
	#print sock.recv(1024)
	time.sleep(1)

