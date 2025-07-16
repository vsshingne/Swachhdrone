AI-Powered Drone Surveillance System
A complete end-to-end drone-based AI system for automatic detection and reporting of garbage dumps in public areas. The system integrates drone video feed processing, object detection using YOLOv8, real-time cloud sync via Firebase, and an Android mobile application for task management by municipal workers.

Overview
This project uses a drone to monitor areas and detect garbage using AI. The drone streams live video and GPS data to a Linux-based edge device (PC), which processes the feed in real-time using a YOLOv8 model trained to recognize garbage piles.

Once garbage is detected:

The system captures a snapshot.

Extracts the GPS location.

Uploads the image to Firebase Storage.

Creates a new task in Firebase Firestore.

Municipal workers are notified of new tasks via an Android app, where they can view, accept, and complete the tasks with map-based navigation.

System Architecture
Drone and Edge AI (Linux PC)
Drone sends live camera feed and GPS data to the edge device.

The edge device runs a Python script that:

Captures frames using OpenCV.

Runs garbage detection using YOLOv8.

If garbage is detected:

Saves an image.

Captures the GPS location.

Uploads the image to Firebase Storage.

Creates a task document in Firestore (activeTasks collection).

Android App
Built using Kotlin and Firebase SDK.

Displays all tasks categorized into:

Pending

Ongoing

Completed

Provides:

Image view

Location view using Google Maps

Button to open shortest path directions via Google Maps

Actions to update task status

Tech Stack
Component	           |         Technology Used
Object Detection	   |       YOLOv8 (Ultralytics)
Video Feed Handling	 |         OpenCV + Python
Edge Device	         |             Linux PC
Mobile App	         |          Android (Kotlin)
Backend	             | Firebase Firestore, Firebase Storage
Authentication	     |       Firebase Authentication
Mapping	             |         Google Maps SDK

Android App Features
Secure login using Firebase Authentication

Real-time sync with Firestore for task updates

Categorized task management (Pending, Ongoing, Completed)

Google Maps integration to show exact garbage location

Shortest path navigation using Google Maps intent

Task status updates with one tap

Edge-Side Script
Script: detect_and_upload.py

This Python script:

Loads a custom-trained YOLOv8 model (best.pt)

Captures frames from the drone's video feed

Detects garbage objects in real-time

On detection:

Saves the image locally

Reads GPS coordinates

Uploads the image to Firebase Storage

Creates a Firestore document with fields like:

imageUrl

location (GeoPoint)

status ("pending")

assignedTo (optional)

Future Improvements
Add drone autopilot integration using GPS waypoints

Optimize model inference using TensorRT or ONNX

Implement a web dashboard for municipal monitoring

Add notification support for workers

Build a real-time heatmap of detected garbage zones
