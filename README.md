### image-service   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/image-service.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/image-service)

This grails application provides the webservices and backend for the storage of all images in the Atlas.
It includes:

* Support for very large images
* Extensible key/value pair storage for image metadata
* Support for subimaging and maintaining the relationships between parent and child images
* Exif extraction
* Tile view for large images compatible with GIS Javascript clients such as LeafletJS, OpenLayers and Google Maps
* Webservices for image upload
* Generate of derivative images for thumbnail presentation
* Tagging support
* Creation of albums of images and bulk tagging

There is a couple of other repositories linked to this

* [images-client-plugin](https://github.com/AtlasOfLivingAustralia/images-client-plugin) - a grails plugin to provide a Javascript based viewer to be used in other applications requiring a image viewer
* [image-tiling-agent](https://github.com/AtlasOfLivingAustralia/image-tiling-agent) - a utility to run tiling jobs for the image-service. This is intended to used on multiple machine as tiling is CPU intensive and best parallelised.
* [image-loader](https://github.com/AtlasOfLivingAustralia/image-loader) - utility for bulk loading images into the image-service
