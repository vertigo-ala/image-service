package au.org.ala.images

import java.text.SimpleDateFormat

class LogService {

    static transactional =  false

    def log(String message) {
        def sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        println "[${sdf.format(new Date())}] ${message}"
    }

    def error(String message, Throwable error) {
        log(message);
        log("Error: ${error.message}")
    }

    def debug(String message) {
        log("DEBUG: ${message}");
    }

}
