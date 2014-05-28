package au.org.ala.images

class DialogController {

    def areYouSureFragment() {
        def message = params.message
        def affirmativeText = params.affirmativeText ?: "Yes"
        def negativeText = params.negativeText ?: "No"

        [message: message, affirmativeText: affirmativeText, negativeText: negativeText]
    }

    def addUserMetadataFragment() {
    }

    def pleaseWaitFragment() {
        [message: params.message ?: "Please wait..."]
    }
}
