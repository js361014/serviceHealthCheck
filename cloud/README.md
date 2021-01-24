This project contains the automation that sets up the service stack in GCP.

# Requirements
1. `gcloud` is installed (installation can be done
  via `curl https://sdk.cloud.google.com | bash`)
1. To configure a project run `gcloud config set project`
1. `gcloud` is initialized (if it's not then run `gcloud init` and proceed
  with the instructions to log in and setup the project)
1. `gcloud services enable container.googleapis.com`
1. `gcloud auth configure-docker`

# How to deploy changes
1. `export PROJECT_NAME=YOUR_PROJ_NAME`
1. `./setup.sh` to setup the cluster
1. `./push_images.sh` to build the application images and push them to the GCR
1. `./deploy.sh` to create kubernetes jobs/services to run the app

# How to test it?
1. Run `kubectl get service frontend` in order to get the `EXTERNAL_IP`. If it isn't there, you most probably have to wait for a few more seconds.
1. Add a target to the application. Please note that you should enter your `EXTERNAL_IP` 
  ```
  curl -d '{"host":"https://www.google.com/","port":"80", "firstAdmin":"egrpreyd@sharklasers.com", "secondAdmin":"egrpreyd@sharklasers.com", "sendNotificationAfter": 10, "resendNotificationAfter": 60}' -H 'Content-Type: application/json' http://$EXTERNAL_IP/addresses
  ```
1. Check logs of the updater:
  ```
  kubectl logs -f -l 'app=updater'
  ```
  You'll see that you're getting info about our target being unhealthy. Indeed, we're sending HTTPS request to port 80. However, you can't see emails being sent. It's because we'd like to get at least one healthy check – maybe the newly registered application is still being setup?
1. Since our target will never be healthy, we'll have to manually do it manually. First, get the name of the POSTGRES_POD:
  ```
  kubectl get pods -l 'app=postgres'
  ```   
  Run the following command to add a healthy check to all the targets at the current time. Note that you have to insert your `POSTGRES_POD`
  ```
  kubectl exec $POSTGRES_POD --stdin --tty -- /usr/bin/psql -U postgres -c "UPDATE addresses set last_healthy = now();"
  ```
1. Now that we have one healthy check, our checker will start working. You can go back the logs from the updater. After some time you'll see that an email has been sent to the first admin.
1. At this point, you'll have 1 minute before an email fill be sent to the second admin. You can try to confirm that an admin is taking care of the problem. Don't forget to insert your `EXTERNAL_IP`.
  ```
  curl http://$EXTERNAL_IP/duty-confirm?id=1
  ```
1. Now, our app will wait for the target to get first healthy check – admin is working on an issue.
1. Again, you can manually add healthy check to the database as our target will never be healthy.
1. Now, you can wait for a little longer and make sure that the mail will be sent to the second admin in case the first admin doesn't respond.
