# Web Interface for the Dataverse Trusted Remote Storage Agent

## To stand up a test deployment:

	$ docker pull odumunc/trsa-web:2.0
	
	$ docker run --name trsa-web trsa-web:2.0
	
Then use a web browser to visit the assigned IP:

	$ links http://172.17.0.2:8080/trsa-web-2.0