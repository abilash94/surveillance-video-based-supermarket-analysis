'''
	DNS returns IP for the service requested

	READ request - 0
	WRITE request - 1

	get IP:
		0 < service name > 
	return get IP (if IP present):
		< ip >
	return get IP (if IP not present):
		:(

	set IP:
		1 < service name > < ip > < port >
	return set IP:
		(y)

'''


import argparse
import time
import threading
import signal
import socket
import sys

#	arguments
ap = argparse.ArgumentParser()
ap.add_argument("-p", "--port", required=False, help="port to run dns on")
args = vars(ap.parse_args())

#	DNS port
dns_port = 10000
if args.get("port"):
	dns_port = int(args["port"])


service_name = []
service_ip = []
service_port = []

#	get system IP
systemIP = socket.gethostbyname(socket.gethostname())


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
		global service_ip
		global service_name
		request_parts = request.split()
		request_type = request_parts[0]
		if request_type == '0':
			j = 0
			for i in service_name:
				if i == request_parts[1]:
					response = service_ip[j] + " " + service_port[j]
					print "DNS response " + response
					return response
				j += 1
			return ":("
		else:
			service_name.append(request_parts[1])
			service_ip.append(request_parts[2])
			service_port.append(request_parts[3])
			print "DNS entry " + request_parts[1] + " " + request_parts[2] + " " + request_parts[3]
			return "(y)" + '\n'
		return ":("

class Server:
	sockets = []
	addresses = []


	def __init__(self, name, port):


		# Create a TCP/IP socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		server_address = ('0.0.0.0', port)

		print 'starting ' + name + ' server up on ' + systemIP + ' %s port %s' % server_address
		sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		sock.bind(server_address)

		# Listen for incoming connections
		sock.listen(1)

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

	def close_connections(self):
		print ('Closing Connections')
		j = 0
		for i in sockets:
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
	set_server_instance(Server(name, port))



create_server(dns_port, "DNS")