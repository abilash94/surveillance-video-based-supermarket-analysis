import socket
import sys

def resolve_ip(dns_ip, dns_port, service):
	resolved = 0
	try:
		# Create a TCP/IP socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# Connect the socket to the port where the server is listening
		server_address = (dns_ip, dns_port)
		print "Resolving IP for '" + service + "'"
		sock.connect(server_address)
		sock.sendall("0 " + service + " ")
		resolved = sock.recv(100)
		print "Resolved: " + resolved
	finally:
		sock.close()
		return resolved

def set_ip(dns_ip, dns_port, service, ip, port):
	success = False
	try:
		# Create a TCP/IP socket
		sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

		# Connect the socket to the port where the server is listening
		server_address = (dns_ip, dns_port)
		print "Setting IP for service '" + service + "' " + ip + " " + str(port)
		sock.connect(server_address)
		sock.sendall("1 " + service + " " + ip + " " + str(port))
		success = True
		#sock.recv(100)
	finally:
		sock.close()
		return success
