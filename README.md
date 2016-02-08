# surveillance-video-based-supermarket-analysis
analyse people activity in supermarket from surveillance video (academic project)

requirements:
	python 2.7
	numpy
	imutils
	opencv 3.1.0

components:

	DNS - for figuring out the IP of systems running various services
	
	videoStream service - server which gets the video stream from a video source 
						and distributes to client nodes

	personTrackClient - client service which tracks humans in an image frame


overall flow:

	DNS should run on a system
		-p --port 		Port for DNS (defaulted)

	videoStream service must be started 
		-i --dnsip  	IP of the DNS (defaulted)
		-d --dnsport 	Port of the DNS (defaulted)
		-p --port 		Port for this service to run (defaulted)
		-v --video 		Video source (leaving this empty will take the webcam as source) (defaulted)

	personTrackClient service 
		-i --dnsip  	IP of the DNS (defaulted)
		-d --dnsport 	Port of the DNS (defaulted)

	videoStream service should be given 's' as input, which will then start to stream

