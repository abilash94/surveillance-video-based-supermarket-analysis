import cv2
import time
import threading
import signal
import socket
import sys
import argparse
import dnsclient
import imutils
import numpy

service_name = "videoStream"

#	arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--dnsip", required=False, help="ip of dns")
ap.add_argument("-d", "--dnsport", required=False, help="port of dns")
ap.add_argument("-p", "--port", required=False, help="port to run videoStream on")
ap.add_argument("-v", "--video", required=False, help="video to stream")
args = vars(ap.parse_args())


if args.get("dnsip"):
	dnsip = args["dnsip"]
else:
	dnsip = "127.0.0.1"
if args.get("dnsport"):
	dnsport = int(args["dnsport"])
else:
	dnsport = 10000
if args.get("port"):
	videoStream_port = int(args["port"])
else:
	videoStream_port = 10002
if args.get("video"):
	videoSource = args["video"]
else:
	videoSource = 0


serverStarted = 0
videoStream_clientsSocket = []

#	server

Server_Instance = 0
class ServerThread(threading.Thread):
	def __init__(self, client_sock, client_address):
		threading.Thread.__init__( self )
		self.client_sock = client_sock
		self.client_address = client_address

	def run(self):
		request = self.client_sock.recv(100)
		if request == "":
			self.close()
			return
		else:
			self.client_sock.sendall(self.process_request(request))

	def close(self):
		print 'Closing Connection ', self.client_address
		try:
			self.client_sock.close()
		finally:
			pass

	def process_request(self, request):
		global videoStream_clients
		if request == "":
			try:
				self.client_sock.close()
			finally:
				print "Disconnected from " + request
				return "Disconnected"

		print "Connected with " + request

		#videoStream_clientsIP.append(request_parts[0])
		#videoStream_clientsPort.append(int(request_parts[1]))
		videoStream_clientsSocket.append(self.client_sock)
		return "(y)"



class Server(threading.Thread):
	sockets = []
	addresses = []

	def __init__(self, name, port):
		threading.Thread.__init__( self )
		self.name = name
		self.port = port

	def run(self):
		global serverStarted
		try:
			# Create a TCP/IP socket
			sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			server_address = ('0.0.0.0', self.port)
			print 'starting ' + self.name + ' server up on ' + socket.gethostbyname(socket.getfqdn()) + ' %s port %s' % server_address
			sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			sock.bind(server_address)

			# Listen for incoming connections
			sock.listen(1)
			serverStarted = 1
			while True:
			    # Wait for a connection
			    print 'waiting for a connection'
			    connection, client_address = sock.accept()
			    self.sockets.append(connection)
			    self.addresses.append(client_address)
			    print 'connection from ', client_address
			    try:
			        #print >>sys.stderr, 'connection from', client_address

					new_thread = ServerThread(connection, client_address)
					new_thread.start()
					
			            
			    finally:
			        # Clean up the connection
			        #connection.close()
			        pass
		except:
			print "Unable to start videoStream server on given port"
			serverStarted = -1
			sys.exit(0)

	def close_connections(self):
		print ('Closing Connections')
		j = 0
		for i in self.sockets:
			print self.addresses[j]
			j += 1
			i.close()


def set_server_instance(ins):
	global Server_Instance 
	Server_Instance = ins

def signal_handler(signal, frame):
	try:
		Server_Instance.close_connections()
	finally:
		sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

def create_server(port, name):
	instance = Server(name, port)
	set_server_instance(instance)
	instance.start()



#	create server
create_server(videoStream_port, service_name)
while serverStarted == 0:
	pass
if serverStarted == -1:
	print "Exiting"
	sys.exit(1)

#	register with DNS
result = dnsclient.set_ip(dnsip, dnsport, service_name, socket.gethostbyname(socket.getfqdn()), videoStream_port)
if result == 0:
	print "DNS not running, or check IP and port of DNS"
	Server_Instance.close_connections()

	sys.exit(1)

#	signal handler
def signal_handler(signal, frame):
	try:
		pass
	finally:
		sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

#	wait for 's' to start streaming
while True:
	start = raw_input("Enter 's' to start streaming..\n")
	if start == 's':
		break


#	start streaming
camera = cv2.VideoCapture(videoSource)
print "Streaming..."

#	loop for streaming
quit = False
length = len(videoStream_clientsSocket)
while True:
	
	for i in range(length):
		
		grabbed, image = camera.read()
		#time.sleep(0.01)
		if not grabbed:
			quit = True
			break
			#sys.exit(0)

		image = imutils.resize(image, width=min(400, image.shape[1]))
		
		try:
			#print image
			#videoStream_clientsSocket[i].recv(1)
			encode_param=[int(cv2.IMWRITE_JPEG_QUALITY),90]
			result, imgencode = cv2.imencode('.jpg', image, encode_param)
			data = numpy.array(imgencode)
			stringData = data.tostring()
			videoStream_clientsSocket[i].send( str(len(stringData)).ljust(16));
			videoStream_clientsSocket[i].send( stringData );
			#videoStream_clientsSocket[i].sendall(stringData)
		except:
			print "EXCEP"

	if quit:
		break

#60268


length = len(videoStream_clientsSocket)

for i in range(length):
	videoStream_clientsSocket[i].close()
print "Exiting"

camera.release()