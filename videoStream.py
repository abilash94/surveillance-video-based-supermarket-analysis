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
import urllib2

service_name = "videoStream"
host = "192.168.1.4:8080"
phoneAsWebcam = False

#	arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--dnsip", required=False, help="ip of dns")
ap.add_argument("-d", "--dnsport", required=False, help="port of dns")
ap.add_argument("-p", "--port", required=False, help="port to run videoStream on")
ap.add_argument("-v", "--video", required=False, help="video to stream")
ap.add_argument("-w", "--webip", required=False, help="IP of phone as webcam")
ap.add_argument("-b", "--block", required=False, help="specify if the server should block till the client signals to proceed")
args = vars(ap.parse_args())

#	assign arguments
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
if args.get("webip"):
	phoneAsWebcam = True
	host = args["webip"]
if args.get("block"):
	block_by_client = args["block"]
else:
	block_by_client = True


#	IP Webcam host URL
hoststr = 'http://' + host + '/video'

#	flag for server started
serverStarted = 0

#	sockets of clients connected with the videoStream server
videoStream_clientsSocket = []


#	get system IP
systemIP = socket.gethostbyname(socket.gethostname())


#	server
Server_Instance = 0
class ServerThread(threading.Thread):
	def __init__(self, client_sock, client_address):
		threading.Thread.__init__( self )
		self.client_sock = client_sock
		self.client_address = client_address

	def run(self):
		global videoStream_clients

		#	receive data from socket 
		request = self.client_sock.recv(100)

		#	terminate if an empty msg is sent
		if request == "":
			self.close()
			self.terminate_thread()

		#	add client to socketsList and reply with success msg
		else:
			#	send success response
			self.client_sock.sendall(self.process_request(request))
			#	add to socketsList
			videoStream_clientsSocket.append(self.client_sock)

		#	terminate this server thread
		self.terminate_thread()

	#	helper for terminating this server thread
	def terminate_thread(self):
		sys.exit(0)

	#	close connection with the client
	def close(self):
		print 'Closing Connection ', self.client_address
		try:
			self.client_sock.close()
		finally:
			pass

	#	generate response
	def process_request(self, request):
		if request == "":
			try:
				self.client_sock.close()
			finally:
				print "Disconnected from " + request
				return "Disconnected"

		print "Connected with " + request

		
		return "(y)"



class Server(threading.Thread):
	sockets = []
	addresses = []

	def __init__(self, name, port):
		threading.Thread.__init__( self )
		self.name = name
		self.port = port

	#	main server thread
	def run(self):
		global serverStarted
		try:
			# Create a TCP/IP socket
			sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			server_address = ('0.0.0.0', self.port)

			print 'starting ' + self.name + ' server up on ' + systemIP + ' %s port %s' % server_address
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

	#	close all connections to clients
	def close_connections(self):
		print ('Closing Connections')
		j = 0
		for i in self.sockets:
			print self.addresses[j]
			j += 1
			try:
				i.close()
			finally:
				pass

#	set main server instance
def set_server_instance(ins):
	global Server_Instance 
	Server_Instance = ins

def signal_handler(signal, frame):
	try:
		Server_Instance.close_connections()
	finally:
		sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

#	create main server
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
result = dnsclient.set_ip(dnsip, dnsport, service_name, systemIP, videoStream_port)
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
if not phoneAsWebcam:
	camera = cv2.VideoCapture(videoSource)
	print "Streaming..."
else:
	try:
		stream=urllib2.urlopen(hoststr)
		bytes=''
	except:
		print "Service not running\nExiting"
		sys.exit(1)

#	loop for streaming
quit = False

currentFrameID = 1
while True:
	length = len(videoStream_clientsSocket)
	#print length
	#break
	
	for i in range(length):
		if not phoneAsWebcam:
			grabbed, image = camera.read()
		else:
			#time.sleep(1)
			bytes+=stream.read(1024)
			a = bytes.find('\xff\xd8')
			b = bytes.find('\xff\xd9')
			if a!=-1 and b!=-1:
				jpg = bytes[a:b+2]
				bytes= bytes[b+2:]
				image = cv2.imdecode(numpy.fromstring(jpg, dtype=numpy.uint8),cv2.IMREAD_COLOR)
				grabbed = True
			else:
				grabbed = False





		#time.sleep(0.01)
		if not grabbed and phoneAsWebcam:
			continue
		elif not grabbed and not phoneAsWebcam:
			quit = True
			break
			#sys.exit(0)

		if not phoneAsWebcam:
			if image.shape[0] > 400:
				image = imutils.resize(image, height=400)
			elif image.shape[1] > 400:
				image = imutils.resize(image, width=400)
		
		try:
			if phoneAsWebcam:
				stringData = jpg

			#	convert image into string format to be sent over socket
			else:
				encode_param=[int(cv2.IMWRITE_JPEG_QUALITY),90]
				result, imgencode = cv2.imencode('.jpg', image, encode_param)
				data = numpy.array(imgencode)
				stringData = data.tostring()
			videoStream_clientsSocket[i].send( str(len(stringData)).ljust(16) + str(currentFrameID).ljust(16));
			
			#	if the server is to blocked by client, block
			if block_by_client:
				resp = videoStream_clientsSocket[i].recv(1)
			else:
				resp = "1"

			#	if the client 
			if resp == "1":
				videoStream_clientsSocket[i].send( stringData );
				currentFrameID += 1
			else:
				videoStream_clientsSocket[i].close()
				#	remove client socket from sockets list
				del videoStream_clientsSocket[i]

				#	this frame will be skipped if the destination is not willing to accept

			#videoStream_clientsSocket[i].sendall(stringData)
		except Exception, err:
			print Exception, err
			print "EXCEP"

	if quit:
		break
		
#60268


length = len(videoStream_clientsSocket)

for i in range(length):
	videoStream_clientsSocket[i].close()
print "Exiting"

#camera.release()