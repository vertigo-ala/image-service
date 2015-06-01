<%@ page import="au.org.ala.web.CASRoles" %>
<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
        <meta name="section" content="home"/>
        <title>ALA Image Service - View Image</title>

        <r:require module="leaflet" />
        <r:require module="leaflet-draw" />

        <style>

        #imageViewer {
            height: 600px;
        }

        a.leaflet-disabled, .leaflet-disabled > a {
            pointer-events: none;
            cursor: default;
        }

        a.viewer-custom-buttons, .viewer-custom-buttons > a {
            background-image: none;
            font-weight: normal;
        }

        #viewer-status {
            margin-bottom: 5px;
            padding: 3px;
            border: 1px solid #ddd;
            border-radius: 3px;

        }

        #viewer-status label {
            display: inline-block;
        }

        </style>
    </head>

    <body class="content">

        <sitemesh:parameter name="useFluidLayout" value="${true}" />

        <img:headerContent title="Viewing Image ${imageInstance?.id}" hideTitle="${true}">
            <%
                pageScope.crumbs = [
                    [link:createLink(controller: 'image', action:'details', id:imageInstance?.id), label: 'Image Details']
                ]
            %>
        </img:headerContent>

        <div class="row-fluid">
            <div class="span12">
                <div id="viewer-status">
                    <label class="checkbox"><g:checkBox name="showSubImages" id="showSubImages" /> Show subimages</label>
                    <span id="zoomStatus" class="pull-right"></span>
                    <span id="mouseStatus"></span>
                </div>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span12">
                <div id="imageViewer"></div>
            </div>
        </div>

        <r:script>

            <g:set var="maxZoom" value="${imageInstance?.zoomLevels ? imageInstance.zoomLevels - 1 : 7}" />

            var imageHeight = ${imageInstance.height};
            var imageWidth = ${imageInstance.width};

            var imageScaleFactor =  Math.pow(2, ${maxZoom});
            var centerx = ${imageInstance.width / 2} / imageScaleFactor;
            var centery = ${imageInstance.height / 2} / imageScaleFactor;

            var p1 = L.latLng(${imageInstance.height} / imageScaleFactor, 0);
            var p2 = L.latLng(0, ${imageInstance.width} / imageScaleFactor );
            var bounds = new L.latLngBounds(p1, p2);


            var viewer = L.map('imageViewer', {
                fullscreenControl: true,
                measureControl: {
                    mmPerPixel: ${imageInstance?.mmPerPixel ?: 0},
                    imageScaleFactor: imageScaleFactor,
                    imageWidth: imageWidth,
                    imageHeight: imageHeight,
                    onCalibration: function(pixels) {
                        var opts = {
                            url:"${createLink(controller:'dialog', action:'calibrateImageFragment', id:imageInstance.id)}?pixelLength=" + Math.round(pixels),
                            title: 'Calibrate image scale'
                        };
                        imglib.showModal(opts);
                    }
                },

                minZoom: 2,
                maxZoom: ${maxZoom},
                zoom: 2,
                // maxBounds: bounds,
                center:new L.LatLng(centery, centerx),
                crs: L.CRS.Simple
            });

            function updateZoomStatus() {
                var zoomLevel = viewer.getZoom();
                $("#zoomStatus").html( "Zoom level " + zoomLevel + " of ${maxZoom}" );
            }

            $(document).ready(function () {

                hookShowSubimages();
                updateZoomStatus();

                var urlMask = "<img:imageTileBaseUrl imageId="${imageInstance?.imageIdentifier}"/>/{z}/{x}/{y}.png";
                L.tileLayer(urlMask, {
                    attribution: '',
                    maxNativeZoom: ${maxZoom},
                    continuousWorld: true,
                    tms: true,
                    noWrap: true,
                    bounds: bounds
                }).addTo(viewer);

                // init drawing...
                var drawnItems = new L.FeatureGroup();
                viewer.addLayer(drawnItems);

                var imageHeight = ${imageInstance.height};

                viewer.on('mousemove', function(e) {
                    var ll = e.latlng;
                    var pixelx = Math.round(ll.lng * imageScaleFactor);
                    var pixely = imageHeight - Math.round(ll.lat * imageScaleFactor);
//                    $("#mouseStatus").html( "Mouse: " + pixelx + ", " + pixely);
                });

                viewer.on('zoomend', function(e) {
                    updateZoomStatus();
                });

                <auth:ifAnyGranted roles="${CASRoles.ROLE_ADMIN},${CASRoles.ROLE_USER}">
                // Initialise the draw control and pass it the FeatureGroup of editable layers
                var drawControl = new L.Control.Draw({
                    edit: {
                        featureGroup: drawnItems
                    },
                    draw: {
                        position: 'topleft',
                        circle: false,
                        rectangle: {
                            shapeOptions: {
                                weight: 1,
                                color: 'blue'
                            }
                        },
                        marker: false,
                        polyline: false,
                        polygon: false
                    }

                });
                viewer.addControl(drawControl);

                $(".leaflet-draw-toolbar").last().append('<a id="btnCreateSubimage" class="viewer-custom-buttons leaflet-disabled fa fa-picture-o" href="#" title="Draw a rectangle to create a sub image"></a>');

                $("#btnCreateSubimage").click(function(e) {
                    e.preventDefault();
                    var layers = drawnItems.getLayers();
                    if (layers.length <= 0) {
                        return;
                    }

                    var ll = layers[0].getLatLngs();

                    // Need to calculate x,y,height and width, where x is the min longitude, y = min latitude, height = max latitude - y and width = max longitude - x
                    var minx = imageWidth, miny = imageHeight, maxx = 0, maxy = 0;

                    for (var i = 0; i < ll.length; ++i) {
                        var y = Math.round(imageHeight - ll[i].lat * imageScaleFactor);
                        var x = Math.round(ll[i].lng * imageScaleFactor);

                        if (y < miny) {
                            miny = y;
                        }
                        if (y > maxy) {
                            maxy = y;
                        }
                        if (x < minx) {
                            minx = x;
                        }
                        if (x > maxx) {
                            maxx = x;
                        }
                    }

                    var height = maxy - miny;
                    var width = maxx - minx;

                    var url = "${createLink(controller:'image', action:'createSubimageFragment', id:imageInstance.id)}?x=" + minx + "&y=" + miny + "&width=" + width + "&height=" + height;
                    var opts = {
                        title: "Create subimage",
                        url: url,
                        onClose: function() {
                            drawnItems.clearLayers();
                        }
                    };
                    imglib.showModal(opts);
                });

                viewer.on('draw:created', function (e) {
			        //var type = e.layerType,
				    var layer = e.layer;
				    drawnItems.clearLayers();
			        drawnItems.addLayer(layer);
                    $("#btnCreateSubimage").removeClass("leaflet-disabled");
                    $("#btnCreateSubimage").attr("title", "Create a subimage from the currently drawn rectangle");
		        });

                viewer.on('draw:deleted', function (e) {
                    var button = $("#btnCreateSubimage");
                    button.addClass("leaflet-disabled");
                    button.attr("title", "Draw a rectangle to create a subimage");

                });

                </auth:ifAnyGranted>

            });

            var imageOverlays = new L.FeatureGroup();
            viewer.addLayer(imageOverlays);

            function hookShowSubimages() {
                $("#showSubImages").change(function() {

                    if ($(this).is(":checked")) {
                        $.ajax("${createLink(controller: "webService", action:"getSubimageRectangles", id: imageInstance.imageIdentifier)}").done(function(results) {
                            if (results.success) {
                                for (var subimageIndex in results.subimages) {

                                    var rect = results.subimages[subimageIndex];
                                    var imageId = rect.imageId;
                                    var lng1 = rect.x / imageScaleFactor;
                                    var lat1 = (imageHeight - rect.y) / imageScaleFactor;
                                    var lng2 = (rect.x + rect.width) / imageScaleFactor;
                                    var lat2 = (imageHeight - (rect.y + rect.height)) / imageScaleFactor;
                                    var bounds = [[lat1,lng1], [lat2, lng2]];

                                    var feature = L.rectangle(bounds, {color: "#ff7800", weight: 1, imageId:imageId, className:'subimage-path imageId-' + imageId});
                                    feature.addTo(imageOverlays);
                                    feature.on("click", function(e) {
                                        var imageId = e.target.options.imageId;
                                        if (imageId) {
                                            window.location = "${createLink(controller:'image', action:'details')}?imageId=" + imageId;
                                        }
                                    });
                                }

                                $(".subimage-path").each(function() {
                                    var classNames = $(this).attr("class");
                                    classNames = $.trim(classNames).split(" ");
                                    // Work out the imageId
                                    var imageId = "";
                                    for (index in classNames) {
                                        var className = classNames[index];
                                        var matches = className.match(/imageId[-](.*)/);
                                        if (matches) {
                                            imageId = matches[1];
                                            break;
                                        }
                                    }

                                    if (imageId) {
                                        $(this).qtip({
                                            content: {
                                                text: function(event, api) {
                                                    $.ajax("${createLink(controller:'image', action:"imageTooltipFragment")}?imageId=" + imageId).then(function(content) {
                                                        api.set("content.text", content);
                                                    },
                                                    function(xhr, status, error) {
                                                        api.set("content.text", status + ": " + error);
                                                    });

                                                }
                                            }
                                        });
                                    }
                                });

                            }
                        });
                    } else {
                        imageOverlays.clearLayers();
                    }

                });
            }

        </r:script>

    </body>
</html>
