import time
import threading
import signal
import socket
import sys

Server_Instance = 0
class ServerThread(threading.Thread):
	def __init__(self, client_sock, client_address):
		threading.Thread.__init__( self )
		self.client_sock = client_sock
		self.client_address = client_address

	def run(self):
		request = self.client_sock.recv(0)
		print request

	def close(self):
		client_sock.close()




class Server:
	sockets = []
	addresses = []


	def __init__(self, name, port):


		# Create a TCP/IP socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		server_address = ('localhost', port)
		print >>sys.stderr, 'starting ' + name + ' server up on %s port %s' % server_address
		sock.bind(server_address)

		# Listen for incoming connections
		sock.listen(1)

		while True:
		    # Wait for a connection
		    print >>sys.stderr, 'waiting for a connection'
		    connection, client_address = sock.accept()
		    self.sockets.append(connection)
		    self.addresses.append(client_address)

		    try:
		        #print >>sys.stderr, 'connection from', client_address
				new_thread = ServerThread(connection, client_address)
				new_thread.start()
				print ('connection from ')
				print (client_address)
		            
		    finally:
		        # Clean up the connection
		        #connection.close()
		        print ('')

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