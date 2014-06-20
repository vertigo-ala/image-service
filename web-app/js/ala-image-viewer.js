var imgvwr = {};

(function(lib) {

    var base_options = {
        imageServiceBaseUrl: "http://images.ala.org.au"
    }

    lib.viewImage = function(targetDiv, imageId, options) {
        var mergedOptions = mergeOptions(options, targetDiv, imageId);
        initDependencies(mergedOptions);
        initViewer(mergedOptions);
    };

    function mergeOptions(userOptions, targetDiv, imageId) {
        var mergedOptions = base_options;

        mergedOptions.target = targetDiv;
        mergedOptions.imageId = imageId;

        if (userOptions.imageServiceBaseUrl) {
            mergedOptions.imageServiceBaseUrl = userOptions.imageServiceBaseUrl;
        }

        return mergedOptions;
    }

    function initDependencies(opts) {
    }

    function initViewer(opts) {
        $.ajax( {
            dataType: 'jsonp',
            url: opts.imageServiceBaseUrl + "/ws/getImageInfo/" + opts.imageId,
            crossDomain: true
        }).done(function(image) {

            if (image.success) {
                _createViewer(opts, image);
            }
        });
    }

    function _createViewer(opts, image) {

        var maxZoom = image.tileZoomLevels ? image.tileZoomLevels - 1 : 0;

        var imageScaleFactor =  Math.pow(2, maxZoom);

        var centerx = image.width / 2 / imageScaleFactor;
        var centery = image.height / 2 / imageScaleFactor;

        var p1 = L.latLng(image.height / imageScaleFactor, 0);
        var p2 = L.latLng(0, image.width / imageScaleFactor);
        var bounds = new L.latLngBounds(p1, p2);


        var viewer = L.map('imageViewer', {
            fullscreenControl: true,
            measureControl: {
                mmPerPixel: image.mmPerPixel ? image.mmPerPixel : 0,
                imageScaleFactor: imageScaleFactor,
                imageWidth: image.width,
                imageHeight: image.height
            },
            minZoom: 2,
            maxZoom: maxZoom,
            zoom: 2,
            center:new L.LatLng(centery, centerx),
            crs: L.CRS.Simple
        });

        var urlMask = image.tileUrlPattern;
        L.tileLayer(urlMask, {
            attribution: '',
            maxNativeZoom: maxZoom,
            continuousWorld: true,
            tms: true,
            noWrap: true,
            bounds: bounds
        }).addTo(viewer);


    }

})(imgvwr);
