MyTube
======

![Web interface](http://sismics.com/mytube/mytube.jpg)

What is MyTube?
---------------

MyTube is a lightweight app aimed to provide an alternative to the [YouTube subscriptions feed](https://www.youtube.com/feed/subscriptions) and the [Watch Later playlist](https://www.youtube.com/playlist?list=WL).

MyTube is written in Java, and may be run on any operating system with Java support.

Features
--------

- Grid view
- Responsive UI
- Based on YouTube v3 API
- Searchable

Build & Install
---------------

- Create a Firebase account to store your Watch Later playlist : [https://www.firebase.com/](https://www.firebase.com/) and grab your Firebase ID
- On your [Google API Console](https://console.developers.google.com/project) enable the YouTube API and create a native app and web app identifier
- Download the project at [https://github.com/jendib/mytube/archive/master.zip](https://github.com/jendib/mytube/archive/master.zip)
- Unzip the archive and edit those files :
- src/main/resources/client_secrets.json : Change `GOOGLE_CLIENT_ID` by your Google native app client ID, Change `GOOGLE_CLIENT_SECRET` by your native app client secret
- src/main/resources/client/app/app.js : Change `GOOGLE_CLIENT_ID` by your Google web app client ID
- src/main/resources/client/app/service/Firebase.js : Change `FIREBASE_ID` by your Firebase ID
- Build the project with `mvn clean install -DskipTests` (you will need Maven 3 at least)
- If you are using Docker, build & run the docker image with the provided `Dockerfile`
- If you are not using Docker :
- Expose the content of `src/main/resources/client` on a web server
- Copy the built jar in `target/mytube-*.jar` anywhere on the same machine
- Add [this Cron rule](https://github.com/jendib/mytube/blob/master/crontab) to your machine (don't forget to change the paths)
