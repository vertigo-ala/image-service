var imglib = {};

(function(lib) {

    lib.showModal = function(options) {

        var opts = {
            backdrop: options.backdrop ? options.backdrop : true,
            keyboard: options.keyboard ? options.keyboard: true,
            url: options.url ? options.url : false,
            id: options.id ? options.id : 'modal_element_id',
            height: options.height ? options.height : 500,
            width: options.width ? options.width : 600,
            title: options.title ? options.title : 'Modal Title',
            hideHeader: options.hideHeader ? options.hideHeader : false,
            onClose: options.onClose ? options.onClose : null,
            onShown: options.onShown ? options.onShown : null
        };

        var html = "<div id='" + opts.id + "' class='modal hide' role='dialog' aria-labelledby='modal_label_" + opts.id + "' aria-hidden='true' style='width: " + opts.width + "px; margin-left: -" + opts.width / 2 + "px;overflow: hidden'>";
        if (!opts.hideHeader) {
            html += "<div class='modal-header'><button type='button' class='close' data-dismiss='modal' aria-hidden='true'>x</button><h3 id='modal_label_" + opts.id + "'>" + opts.title + "</h3></div>";
        }
        html += "<div class='modal-body' style='max-height: " + opts.height + "px'>Loading...</div></div>";

        $("body").append(html);

        var selector = "#" + opts.id;

        $(selector).on("hidden", function() {
            if (opts.onClose) {
                opts.onClose();
            }
            $(selector).remove();
        });

        $(selector).on("shown", function() {
            if (opts.onShown) {
                opts.onShown();
            }
        });

        $(selector).modal({
            remote: opts.url,
            keyboard: opts.keyboard,
            backdrop: opts.backdrop
        });

    };

    lib.hideModal = function() {
        $("#modal_element_id").modal('hide');
    };

    lib.areYouSureOptions = {};

    lib.areYouSure = function(options) {

        var modalOptions = {
            url: IMAGES_CONF.areYouSureUrl + "?message=" + encodeURIComponent(options.message),
            title: options.title ? options.title : "Are you sure?"
        };

        lib.areYouSureOptions.affirmativeAction = options.affirmativeAction;
        lib.areYouSureOptions.negativeAction = options.negativeAction;

        imglib.showModal(modalOptions);
    };

    lib.onAlbumSelected = null;

    lib.selectAlbum = function(onSelectFunction) {
        var opts = {
            title: "Select an album",
            url: IMAGES_CONF.selectAlbumUrl
        };
        lib.onAlbumSelected = function(albumId) {
            lib.hideModal();
            if (onSelectFunction) {
                onSelectFunction(albumId);
            }
        };
        imglib.showModal(opts);
    };

    lib.onTagSelected = null;
    lib.onTagCreated = null;

    lib.selectTag = function(onSelectFunction) {
        var opts = {
            width: 700,
            title: "Select a tag",
            url: IMAGES_CONF.selectTagUrl
        };

        lib.onTagSelected = function(tagId) {
            lib.hideModal();
            if (onSelectFunction) {
                onSelectFunction(tagId);
            }
        };
        imglib.showModal(opts);
    };

    lib.createNewTag = function(parentTagId, onCreatedFunction) {

        var opts = {
            title: "Create new tag from path",
            url: IMAGES_CONF.createTagUrl + "?parentTagId=" + parentTagId
        };

        lib.onTagCreated = function(tagId) {
            lib.hideModal();
            if (onCreatedFunction) {
                onCreatedFunction(tagId);
            }
        };
        imglib.showModal(opts);
    };

    lib.onAddMetadata = null;

    lib.promptForMetadata = function(onMetadata) {

        var opts = {
            title: "Add meta data item",
            url: IMAGES_CONF.addUserMetaDataUrl
        };

        lib.onAddMetadata = function(key, value) {
            lib.hideModal();
            if (onMetadata) {
                onMetadata(key, value);
            }
        };

        imglib.showModal(opts);
    };

    lib.bindImageTagTooltips = function() {
        $(".image-tags-button").each(function() {
            var imageId = $(this).closest("[imageId]").attr("imageId");
            if (imageId) {
                $(this).qtip({
                    content: {
                        text: function(event, api) {
                            $.ajax(IMAGES_CONF.imageTagsTooltipUrl + "/" + imageId).then(function(content) {
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
    };

    lib.pleaseWait = function(message, ajaxUrl, resultHandler) {

        var modalOptions = {
            url: IMAGES_CONF.pleaseWaitUrl + "?message=" + encodeURIComponent(message),
            title: "Please wait...",
            onShown: function() {
                $.ajax(ajaxUrl).done(function(result) {
                    if (resultHandler) {
                        resultHandler(result);
                    }
                    lib.hideModal();
                });
            }
        };

        lib.showModal(modalOptions);

    };

    lib.htmlEscape = function(str) {
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');
    };

    lib.htmlUnescape = function(value) {
        return String(value)
            .replace(/&quot;/g, '"')
            .replace(/&#39;/g, "'")
            .replace(/&lt;/g, '<')
            .replace(/&gt;/g, '>')
            .replace(/&amp;/g, '&');
    };

    lib.showSpinner = function(message) {
        var spinner = $(".spinner");
        if (message) {
            spinner.attr("title", message);
        } else {
            spinner.attr("title", "");
        }
        spinner.css("display", "block");
    }

    lib.hideSpinner = function() {
        var spinner = $(".spinner");
        spinner.css("display", "none");
    }

})(imglib);



