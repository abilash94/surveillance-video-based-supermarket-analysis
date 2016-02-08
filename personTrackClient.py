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
import collections
from imutils.object_detection import non_max_suppression

import dnsclient

service_name = "personTrackClient"
service_name_server = "videoStream"

#	arguments
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--dnsip", required=False, help="ip of dns")
ap.add_argument("-d", "--dnsport", required=False, help="port of dns")
#ap.add_argument("-p", "--port", required=False, help="port to run videoStream on")
args = vars(ap.parse_args())

if args.get("dnsip"):
	dnsip = args["dnsip"]
else:
	dnsip = "127.0.0.1"
if args.get("dnsport"):
	dnsport = int(args["dnsport"])
else:
	dnsport = 10000


#	get IP of videoStream
videoStream = dnsclient.resolve_ip(dnsip, dnsport, service_name_server)
if videoStream == 0:
	print "DNS not running, or check IP and port of DNS"
	sys.exit(1)
videoStream_parts = videoStream.split()
print videoStream_parts
videoStreamIP = videoStream_parts[0]
videoStreamPort = int(videoStream_parts[1])

imageReceived = 0
imageReceivedStarted = False
lock = threading.Lock()

frameCount = 0
startTime = 0

startTime = time.time()

def recvall(sock, n):
    # Helper function to recv n bytes or return None if EOF is hit
    data = ''
    while len(data) < n:
        packet = sock.recv(n - len(data))
        if not packet:
            return None
        data += packet
	return data

class ClientThread(threading.Thread):
	def __init__(self, client_sock):
		threading.Thread.__init__( self )
		self.client_sock = client_sock
		
	def run(self):
		global imageReceived
		global imageReceivedStarted
		global lock
		global frameCount
		global startTime
		while True:
			try:
				#self.client_sock.sendall("1")
				#stringData = self.client_sock.recv(10000)
				#data = numpy.fromstring(stringData, dtype='uint8')
				length = recvall(self.client_sock,16)
				#print "len", length
				stringData = recvall(self.client_sock, int(length))
				data = numpy.fromstring(stringData, dtype='uint8')
				decimg=cv2.imdecode(data,1)
				#print "IMAGE .."
				#print decimg
				lock.acquire()
				imageReceived = decimg
				lock.release()
				imageReceivedStarted = True
				frameCount += 1
				#time.sleep(0.01)
				#print time.time() - startTime, frameCount
				print (frameCount / (time.time() - startTime))
			except:
				pass


#	connect as client with the videoStream Server

def videoStreamConnect():
	global videoStreamIP
	global videoStreamPort
	try:
		# Create a TCP/IP socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# Connect the socket to the port where the server is listening
		server_address = (videoStreamIP, videoStreamPort)
		sock.connect(server_address)
		print "Connected with videoStream\n"
		sock.sendall(socket.gethostbyname(socket.getfqdn()))
		response = sock.recv(100)
		if response == '(y)':
			return sock 
		else:
			print "videoStream Service IP unresolved"
			return 0

	except:
		sock.close()
		return 0
	


# initialize the HOG descriptor/person detector
hog = cv2.HOGDescriptor()
hog.setSVMDetector(cv2.HOGDescriptor_getDefaultPeopleDetector())

def trackPerson():
	#start = time.time()
	global imageReceived
	
	lock.acquire()

	image = imageReceived
	
	lock.release()

	orig = image.copy()

	# detect people in the image
	(rects, weights) = hog.detectMultiScale(image, winStride=(4, 4),
		padding=(8, 8), scale=1.05)

	# draw the original bounding boxes
	for (x, y, w, h) in rects:
		cv2.rectangle(orig, (x, y), (x + w, y + h), (0, 0, 255), 2)

	# apply non-maxima suppression to the bounding boxes using a
	# fairly large overlap threshold to try to maintain overlapping
	# boxes that are still people
	rects = numpy.array([[x, y, x + w, y + h] for (x, y, w, h) in rects])
	pick = non_max_suppression(rects, probs=None, overlapThresh=0.65)

	# draw the final bounding boxes
	for (xA, yA, xB, yB) in pick:
		cv2.rectangle(image, (xA, yA), (xB, yB), (0, 255, 0), 2)

	# show some information on the number of bounding boxes
	#filename = imagePath[imagePath.rfind("/") + 1:]
	#filename = "FILE"
	#print("[INFO] {}: {} original boxes, {} after suppression".format(
	#	filename, len(rects), len(pick)))

	# show the output images
	cv2.imshow("Before NMS", orig)
	cv2.imshow("After NMS", image)
	#print time.time() - start


#	get videoStream socket
videoStreamSocket = videoStreamConnect()

if videoStreamSocket == 0:
	print "Error\nExiting"
	sys.exit(0)


#	spawn thread to continuously retrieve images from videoStream
retriever = ClientThread(videoStreamSocket)
retriever.start()

#	signal handler
def signal_handler(signal, frame):
	try:
		pass
	finally:
		sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

#	run loop
while True:
	if not imageReceivedStarted:
		time.sleep(0.001)					#	to avoid high cpu usage
		pass
	else:
		#cv2.imshow("img", imageReceived)
		trackPerson()
		cv2.waitKey(1)

		
print "END"