# gst-video-broadcaster

# Overview 

This project is an Android module designed to securely and remotely control IP cameras from the web while providing real‑time video streaming capabilities.

Its core purpose is to:

* Retrieve IP camera video streams via RTSP
* Process and manage the media pipeline using GStreamer
* Broadcast the video stream via WebRTC for low‑latency preview in web or mobile clients
* Optionally publish live streams to external platforms

# GStreamer Pipeline

The pipeline is dynamically built at runtime using gst_parse_launch and follows this structure:

* rtspsrc – RTSP camera source
* rtpjitterbuffer – Network jitter handling
* rtph264depay / h264parse – H.264 depayloading and parsing
* tee – Splits the stream into multiple branches
* webrtcfeedbackbin – WebRTC preview and signaling
* livepublisherbin – Live stream publishing

This allows simultaneous WebRTC preview and live broadcasting from a single RTSP source.

The module acts as a bridge between Android, native C/C++ code (JNI), GStreamer pipelines, and web‑based signaling/control layers.

# Submodules:

* camera-stream-pipeline: Core Android native streaming module, GStreamer pipeline lifecycle management.
* panel-connection-manager: Web <--> Kontroller protocol implemention (requests parsing & handling, event emitting ..)
* kontroller-services-api: API module (devices controllers interfaces/dtos/business exceptions).
* kontroller-services-api-implem: a fake implementation of  #kontroller-services-api.
* visca-over-ip: Implements VISCA over IP protocol support (PTZ (Pan / Tilt / Zoom) camera controL...).

