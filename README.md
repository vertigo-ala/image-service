### image-service   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/image-service.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/image-service)

This grails application provides the webservices and backend for the storage of all images in the Atlas.
It includes:

* Support for very large images
* Extensible key/value pair storage for image metadata
* Support for subimaging and maintaining the relationships between parent and child images
* Exif extraction
* Tile view for large images compatible with GIS JS clients such as LeafletJS, OpenLayers and Google Maps
* Webservices for image upload
* Generate of derivative images for thumbnail presentation
* Tagging support
* Creation of albums of images and bulk tagging
