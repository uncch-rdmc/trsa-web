# Web Interface for the Dataverse Trusted Remote Storage Agent

## To stand up a test deployment:

	$ docker pull odumunc/trsa-web:2.0
	
	$ docker run --name trsa-web -p 8080:8080 trsa-web:2.0
	
Then use a web browser to visit the assigned IP:

	$ links http://172.17.0.2:8080/trsa-web-2.0
	
	
## To build your own Docker container:

* Edit `trsa.config` to suit your needs, then `docker build` with your desired name and tag.