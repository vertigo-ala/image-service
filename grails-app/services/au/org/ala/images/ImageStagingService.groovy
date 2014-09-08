package au.org.ala.images

import grails.transaction.Transactional
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.web.multipart.MultipartFile

@Transactional
class ImageStagingService {

    def grailsApplication
    def settingService
    def logService

    ResultsPageList<StagedFile> getFileList(String userId, GrailsParameterMap params) {
        if (!userId) {
            return new ResultsPageList<StagedFile>([], 0)
        }

        def queryParams = [:]

        queryParams.max = params.int('max') ?: -1
        queryParams.offset = params.int('offset') ?: 0
        queryParams.order = params.order ?: 'asc'
        queryParams.sort = params.sort ?: 'dateStaged'

        def page = StagedFile.findAllByUserId(userId, queryParams)
        int totalCount = StagedFile.countByUserId(userId)

        return new ResultsPageList<StagedFile>(page, totalCount)
    }

    public StagedFile stageFile(String userId, MultipartFile file) {
        if (!userId || !file) {
            return null
        }

        def userdir = getStagingDirectory(userId)
        def originalFilename = file.originalFilename

        def newFile = new File(combine(userdir, originalFilename));
        file.transferTo(newFile);
        // If we get here the transfer was successful, so we can create a record for it in the database
        def stagedFile = new StagedFile(userId: userId, filename: originalFilename, dateStaged: new Date())
        stagedFile.save(failOnError: true, flush: true)
        return stagedFile
    }

    private String getStagingDirectory(String userId) {
        def basedir = grailsApplication.config.imageservice.imagestore.root as String
        def userdir = new File(combine(basedir, "staging/${userId}"))
        if (!userdir.exists()) {
            userdir.mkdirs()
        }
        return userdir.toPath()
    }

    private String getStagingDataFile(String userId) {
        combine(getStagingDirectory(userId), "/datafile/datafile.txt")
    }

    public static String combine(String path1, String path2) {
        new File(new File(path1), path2).getPath()
    }

    public String getStagedFileLocalPath(StagedFile stagedFile) {
        def userdir = getStagingDirectory(stagedFile.userId)
        return combine(userdir, stagedFile.filename)
    }

    public void purgeOldStagedFiles() {
        def lifetimeInHours = settingService.getStagedFileLifespanInHours() ?: 24

        // work out the cutoff date...
        def millis = lifetimeInHours * 60 * 60 * 1000 // minutes * seconds * millis
        def cutoffDate = new Date(new Date().getTime() - millis)
        def purgeList = StagedFile.findAllByDateStagedLessThan(cutoffDate)
        if (purgeList) {
            logService.debug("${purgeList.size()} staged files older than ${lifetimeInHours} hours.")
            purgeList.each { stagedFile ->
                deleteStagedFile(stagedFile)
            }
        } else {
            logService.debug("No eligible staged files found to purge.")
        }
    }

    public boolean deleteStagedFile(StagedFile stagedFile) {
        // first delete the file from the disk...
        def file = new File(getStagedFileLocalPath(stagedFile))
        if (file.exists()) {
            if (!file.delete()) {
                file.deleteOnExit()
            }
        }
        // then remove from the database
        stagedFile.delete()
    }

    def uploadDataFile(String userId, MultipartFile multipartFile) {
        def f = new File(getStagingDataFile(userId))
        if (!f.parentFile.exists()) {
            f.mkdirs()
        }
        if (f.exists()) {
            // delete any existing file first
            f.delete()
        }

        multipartFile.transferTo(f);
    }

    public boolean hasDataFileUploaded(String userId) {
        def f = new File(getStagingDataFile(userId))
        return f.exists()
    }

    public String getDataFileUrl(String userId) {
        def root = grailsApplication.config.imageservice.apache.root
        return root + "/staging/${userId}/datafile/datafile.txt"
    }

    def deleteDataFile(String userId) {
        def f = new File(getStagingDataFile(userId))
        if (f.exists()) {
            f.delete()
        }
    }

    public String[] getDataFileColumns(String userId) {
        def f = new File(getStagingDataFile(userId))
        if (f.exists()) {
            def lines = FileUtils.readLines(f)
            if (lines && lines.size() > 0) {
                return lines[0].split(",")
            }
        }
        return []
    }

}
