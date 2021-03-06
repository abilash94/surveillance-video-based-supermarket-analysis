# surveillance-video-based-supermarket-analysis
analyse people activity in supermarket from surveillance video (academic project)

<b>requirements:</b>

	python 2.7
	numpy
	imutils
	opencv 3.1.0
	javac 1.8.0_60

<b>components:</b>

	DNS - for figuring out the IP of systems running various services
	
	videoStream service - server which gets the video stream from a video source 
						and distributes to client nodes

	personTrackClient - client service which tracks humans in an image frame

	continuousTracking - get tracked data from client(s) and map person(s) movement(s)


<b>overall flow:</b>

	DNS should run on a system
		-p --port 		Port for DNS (defaulted)
	
	ozeki service must be started and registered using ozeki_set_ip.py in backend repo
		-i --dnsip  	IP of the DNS (defaulted)
		-d --dnsport 	Port of the DNS (defaulted)
	
	rails server for maintaining billing cart must be started.
		super-market-surveillance-backend (https://github.com/abilash94/super-market-surveillance-backend) contains a rails application
		it must be started using DBbackend.sh (
			DBbackend.py (params: contains port on which rails server will start, IP and port of DNS), 
			resolveAndServe.py (function: get IP and port of ozeki service and serve to the rails server 
			for retrieval via a HTTP request; params: IP and port of DNS), 
			start rails server on the specified port ), 
		which will automatically register it's IP in the DNS
	
	videoStream service must be started 
		-i --dnsip  	IP of the DNS (defaulted)
		-d --dnsport 	Port of the DNS (defaulted)
		-p --port 		Port for this service to run (defaulted)
		-v --video 		Video source (leaving this empty will take the webcam as source) (defaulted)
		-w --webip		IP and port of phone running as webcam
		-b --block 		specify if the server should block till the client signals to proceed (defaulted = True)

	continuousTracking service must be started
		-d 				IP of DNS (defaulted)
		-p 				Port of DNS (defaulted)
		example: "java continuousTracking -d 127.0.0.1 -p 10000"	(follow this exact format, for now :P)

	personTrackClient service 
		-i --dnsip  	IP of the DNS (defaulted)
		-d --dnsport 	Port of the DNS (defaulted)
		-b --block 		specify if the client should send signal to proceed (defaulted = True)

	videoStream service should be given 's' as input, which will then start to stream

<b>Note:</b> <br>	defaulted means input is not needed for basic running on the same system. (ie) if all the services are run on the same system, no parameters are needed
